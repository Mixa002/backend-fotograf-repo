package com.masasajt.backendfotograf.service;


import com.masasajt.backendfotograf.dto.AlbumDTO;
import com.masasajt.backendfotograf.mapper.AlbumMapper;
import com.masasajt.backendfotograf.model.Album;
import com.masasajt.backendfotograf.model.Image;
import com.masasajt.backendfotograf.model.User;
import com.masasajt.backendfotograf.repository.AlbumRepository;
import com.masasajt.backendfotograf.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {
    private final AlbumRepository albumRepo;
    private final UserService userService;
    private final AlbumMapper albumMapper;
    private final ImageRepository imageRepo;
    private final CloudflareR2Service cloudflareR2Service;

    public List<AlbumDTO> getPublicAlbums(){
        List<Album> publicAlbums = albumRepo.findByIsPrivateFalse();
        return albumMapper.toAlbumDTOList(publicAlbums);
    }

    public List<AlbumDTO> getPrivateAlbumsForClient(String username){
        List<Album> privateAlbums = albumRepo.findByIsPrivateTrueAndClientUsername(username);
        return albumMapper.toAlbumDTOList(privateAlbums);
    }

    public List<AlbumDTO> getAllAlbums(){
        List<Album> allAlbums = albumRepo.findAll();
        return allAlbums.stream()
                .map(albumMapper::toAlbumDTO)
                .toList();
    }

    @Transactional
    public AlbumDTO createAlbum(String name, boolean isPrivate, String clientUsername){

        System.out.println("--- KREIRANJE ALBUMA ---");
        System.out.println("Primljeno ime: " + name);
        System.out.println("Primljeno isPrivate: " + isPrivate);
        System.out.println("Primljen klijent: " + clientUsername);
        System.out.println("------------------------");

        Album album = new Album();
        album.setName(name);
        album.setPrivate(isPrivate);
        album.setDate(LocalDate.now());
        if(isPrivate && clientUsername != null && !clientUsername.isBlank()){
            User client = userService.findByUsername(clientUsername);
            album.setClient(client);
        }

        Album savedAlbum = albumRepo.saveAndFlush(album);
        AlbumDTO dto = albumMapper.toAlbumDTO(savedAlbum);
        if (savedAlbum.getClient() != null) {
            dto.setClientUsername(savedAlbum.getClient().getUsername());
        }

        return dto;
    }

    @Transactional
    public AlbumDTO uploadAddImagesToAlbum(Long albumId, MultipartFile[] files){
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album nije pronadjen!"));

        for (MultipartFile file: files){
            if(!file.isEmpty()){
                try{
                    String imageURL = cloudflareR2Service.uploadImage(file);

                    Image image = new Image();
                    image.setUrl(imageURL);
                    image.setAlbum(album);

                    imageRepo.save(image);

                    album.addImage(image);
                }catch (Exception e){
                    System.out.println("Greska pri uploadu pojedinacnog fajla: " + e);
                }
            }
        }

        Album updatedAlbum = albumRepo.save(album);
        return albumMapper.toAlbumDTO(updatedAlbum);
    }

    public AlbumDTO getAlbumById(Long id){
        Album album =  albumRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Album nije pronadjen!"));
        return albumMapper.toAlbumDTO(album);
    }

    @Transactional(readOnly = false)
    public void deleteAlbum(Long albumId){
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album sa tim ID-jem ne postoji!"));

        // 2. Prođi kroz sve slike i obriši ih sa Cloudflare R2 preko njihovog URL-a
        if (album.getImages() != null) {
            for (Image image : album.getImages()) {
                if (image.getUrl() != null) {
                    cloudflareR2Service.deleteImageByUrl(image.getUrl());
                }
            }
        }

        albumRepo.delete(album);
    }
}
