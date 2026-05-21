package com.masasajt.backendfotograf;

import com.masasajt.backendfotograf.model.Role;
import com.masasajt.backendfotograf.model.User;
import com.masasajt.backendfotograf.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendFotografApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendFotografApplication.class, args);
    }

    // OVO JE PRAVO MESTO ZA @Bean
    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@masa.com")
                        .password(passwordEncoder.encode("masa123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("--- ADMIN JE KREIRAN U BAZI ---");
            }
        };
    }
}