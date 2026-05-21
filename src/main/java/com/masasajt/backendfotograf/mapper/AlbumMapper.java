package com.masasajt.backendfotograf.mapper;


import com.masasajt.backendfotograf.dto.AlbumDTO;
import com.masasajt.backendfotograf.dto.ImageDTO;
import com.masasajt.backendfotograf.model.Album;
import com.masasajt.backendfotograf.model.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    ImageDTO toImageDTO(Image image);
    List<ImageDTO> toImageDTOList(List<Image> images);

    @Mapping(target = "isPrivate", source = "private")
    // Iz ugnježdenog objekta 'client' (tipa User) izvlačimo samo 'username' i upisujemo u 'clientUsername'
    @Mapping(target = "clientUsername", source = "client.username")
    // Pozivamo custom metodu 'extractCoverImage' da bismo popunili polje 'coverImageUrl'
    @Mapping(target = "coverImageURL", source = "images", qualifiedByName = "extractCoverImage")
    AlbumDTO toAlbumDTO(Album album);

    List<AlbumDTO> toAlbumDTOList(List<Album> albums);

    // Pošto u bazi nemaš posebno polje 'cover_image', ova metoda uzima PRVU sliku iz albuma
    // i postavlja njen URL kao naslovnu sliku za karticu na frontendu.
    @Named("extractCoverImage")
    default String extractCoverImage(List<Image> images) {
        if (images == null || images.isEmpty()) {
            // Ako je album prazan (tek kreiran), vraća prelepi Unsplash fotografski placeholder
            return "https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=500";
        }
        return images.getFirst().getUrl(); // Vraća URL prve slike u listi
    }

}
