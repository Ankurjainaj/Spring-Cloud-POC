package com.example.albumsService.service;

import com.example.albumsService.entity.AlbumsEntity;

import java.util.List;

public interface AlbumsService {

    List<AlbumsEntity> getAlbumsByUserId(String userId);
}
