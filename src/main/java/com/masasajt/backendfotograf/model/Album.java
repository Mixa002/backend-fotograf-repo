package com.masasajt.backendfotograf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    // Indikator da li je album privatan (za klijenta) ili javan (za Recent Work)
    @Column(name = "is_private", nullable = false)
    @JsonProperty("isPrivate")
    private boolean isPrivate;

    // Spajamo album sa klijentom. Ako je album javan, ovo polje će u bazi biti NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private User client;

    // Jedan album sadrži mnogo slika
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();


    // 1. Tvoj ručni sigurnosni getter (Lombok će ga preskočiti i prepustiti tebi)
    public List<Image> getImages() {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        return this.images;
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setAlbum(this); // Automatski postavlja i kontra stranu veze
    }
}