package com.masasajt.backendfotograf.controller;


import com.masasajt.backendfotograf.dto.AuthResponseDTO;
import com.masasajt.backendfotograf.dto.LoginRequestDTO;
import com.masasajt.backendfotograf.dto.RegisterRequestDTO;
import com.masasajt.backendfotograf.dto.UserDTO;
import com.masasajt.backendfotograf.model.User;
import com.masasajt.backendfotograf.service.AuthService;
import com.masasajt.backendfotograf.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService){
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequest){
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerClient(@RequestBody RegisterRequestDTO registerRequest){
        UserDTO newClient = userService.registerNewClient(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
        );
        return ResponseEntity.ok(newClient);
    }
}
