package com.masasajt.backendfotograf.service;


import com.masasajt.backendfotograf.dto.UserDTO;
import com.masasajt.backendfotograf.mapper.UserMapper;
import com.masasajt.backendfotograf.model.Role;
import com.masasajt.backendfotograf.model.User;
import com.masasajt.backendfotograf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepo.findAll();
        return userMapper.toUserDTOList(users);
    }

    public User findByUsername(String username){
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik sa tim korisnickim imenom ne postoji"));
    }

    @Transactional
    public UserDTO registerNewClient(String username, String password, String email){
        if(userRepo.findByUsername(username).isPresent()){
            throw new RuntimeException("Korisnicko ime vec postoji u bazi!");
        }
        if(userRepo.findByEmail(email).isPresent()){
            throw new RuntimeException("Email adresa je vec u upotrebi!");
        }
        User newClient = User.builder()
                .username(username).email(email).password(passwordEncoder.encode(password))
                .role(Role.USER).build();

        User savedClient = userRepo.save(newClient);
        return userMapper.toUserDTO(savedClient);
    }
}
