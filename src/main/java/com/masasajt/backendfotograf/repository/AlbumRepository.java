package com.masasajt.backendfotograf.repository;

import com.masasajt.backendfotograf.model.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Ova anotacija kaže: "Kada vučeš albume, odmah spoji tabelu i povuci 'client' objekat"
    @EntityGraph(attributePaths = {"client"})
    List<Album> findAll();

    List<Album> findByIsPrivateFalse();

    List<Album> findByIsPrivateTrueAndClientUsername(String username);
}
