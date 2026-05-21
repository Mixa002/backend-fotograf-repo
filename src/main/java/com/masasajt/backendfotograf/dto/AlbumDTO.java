package com.masasajt.backendfotograf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDTO {
    private Long id;
    private String name;
    private LocalDate date;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    private String coverImageURL;
    private List<ImageDTO> images;
    private String clientUsername;
}
