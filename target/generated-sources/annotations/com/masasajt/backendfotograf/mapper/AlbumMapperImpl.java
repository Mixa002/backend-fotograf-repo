package com.masasajt.backendfotograf.mapper;

import com.masasajt.backendfotograf.dto.AlbumDTO;
import com.masasajt.backendfotograf.dto.ImageDTO;
import com.masasajt.backendfotograf.model.Album;
import com.masasajt.backendfotograf.model.Image;
import com.masasajt.backendfotograf.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T13:09:56+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Homebrew)"
)
@Component
public class AlbumMapperImpl implements AlbumMapper {

    @Override
    public ImageDTO toImageDTO(Image image) {
        if ( image == null ) {
            return null;
        }

        ImageDTO.ImageDTOBuilder imageDTO = ImageDTO.builder();

        imageDTO.id( image.getId() );
        imageDTO.url( image.getUrl() );

        return imageDTO.build();
    }

    @Override
    public List<ImageDTO> toImageDTOList(List<Image> images) {
        if ( images == null ) {
            return null;
        }

        List<ImageDTO> list = new ArrayList<ImageDTO>( images.size() );
        for ( Image image : images ) {
            list.add( toImageDTO( image ) );
        }

        return list;
    }

    @Override
    public AlbumDTO toAlbumDTO(Album album) {
        if ( album == null ) {
            return null;
        }

        AlbumDTO.AlbumDTOBuilder albumDTO = AlbumDTO.builder();

        albumDTO.isPrivate( album.isPrivate() );
        albumDTO.clientUsername( albumClientUsername( album ) );
        albumDTO.coverImageURL( extractCoverImage( album.getImages() ) );
        albumDTO.id( album.getId() );
        albumDTO.name( album.getName() );
        albumDTO.date( album.getDate() );
        albumDTO.images( toImageDTOList( album.getImages() ) );

        return albumDTO.build();
    }

    @Override
    public List<AlbumDTO> toAlbumDTOList(List<Album> albums) {
        if ( albums == null ) {
            return null;
        }

        List<AlbumDTO> list = new ArrayList<AlbumDTO>( albums.size() );
        for ( Album album : albums ) {
            list.add( toAlbumDTO( album ) );
        }

        return list;
    }

    private String albumClientUsername(Album album) {
        User client = album.getClient();
        if ( client == null ) {
            return null;
        }
        return client.getUsername();
    }
}
