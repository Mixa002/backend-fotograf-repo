package com.masasajt.backendfotograf.repository;

import com.masasajt.backendfotograf.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByAlbumId(Long albumId);
}
