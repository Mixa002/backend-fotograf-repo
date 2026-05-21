package com.masasajt.backendfotograf.repository;

import com.masasajt.backendfotograf.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Provera da li već postoji korisnik sa tim email-om ili username-om (za registraciju)
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
