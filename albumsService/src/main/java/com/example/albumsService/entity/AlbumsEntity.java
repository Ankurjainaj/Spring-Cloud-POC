package com.example.albumsService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumsEntity {

    private long id;
    private String albumId;
    private String userId;
    private String name;
    private String description;
}
