package com.masasajt.backendfotograf.controller;


import com.masasajt.backendfotograf.dto.AlbumDTO;
import com.masasajt.backendfotograf.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/public")
    public ResponseEntity<List<AlbumDTO>> getPublicAlbums(){
        System.out.println("HIT ENDPOINT");
        return ResponseEntity.ok(albumService.getPublicAlbums());
    }

    @GetMapping("/my-albums")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums(@AuthenticationPrincipal UserDetails userDetails){
        //AuthenticationPrincipal uzima ulogovanog korisnika iz SecurityContexta(JWT-a)
        String username = userDetails.getUsername();
        return ResponseEntity.ok(albumService.getPrivateAlbumsForClient(username));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Long id){
        System.out.println("--- KONTROLER: TRAŽI SE ALBUM SA ID-jem: " + id + " ---");
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AlbumDTO> createAlbum(
            @RequestParam String name,
            @RequestParam boolean isPrivate,
            @RequestParam(value = "clientUsername", required = false) String clientUsername
    ){
        AlbumDTO createdAlbum = albumService.createAlbum(name, isPrivate, clientUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlbum);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AlbumDTO> addImagesToAlbum(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files
    ){
        // Pozivamo tvoju novu metodu iz AlbumService-a koja radi upload na R2
        AlbumDTO updatedAlbum = albumService.uploadAddImagesToAlbum(id, files);
        return ResponseEntity.ok(updatedAlbum);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id){
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();//http 204
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Samo admin može da povuče i privatne albume
    public ResponseEntity<List<AlbumDTO>> getAllAlbums() {
        List<AlbumDTO> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }
}
