package com.masasajt.backendfotograf.service;

import com.masasajt.backendfotograf.dto.AuthResponseDTO;
import com.masasajt.backendfotograf.dto.LoginRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Autentifikujemo korisnika preko menadžera
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Izvlačimo UserDetails (koji sadrži i uloge) iz uspešne autentifikacije
        org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        // 3. Pretvaramo njegove GrantedAuthority uloge u listu Stringova (npr. ["ROLE_USER"] ili ["ROLE_ADMIN"])
        java.util.List<String> roles = userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        // 4. Generišemo token prosleđujući i username i listu uloga
        String token = jwtService.generateToken(request.getUsername(), roles);

        return new AuthResponseDTO(token);
    }
}
