package com.masasajt.backendfotograf.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Glavni podatak - URL adresa slike na Cloud-u (Cloudinary, R2, itd.)
    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    // Svaka slika mora pripadati isključivo jednom albumu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // Automatski postavljamo datum uploada pre nego što se upiše u bazu
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}