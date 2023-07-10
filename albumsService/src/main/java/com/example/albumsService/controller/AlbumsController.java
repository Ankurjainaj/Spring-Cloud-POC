package com.example.albumsService.controller;

import com.example.albumsService.entity.AlbumsEntity;
import com.example.albumsService.model.AlbumResponseModel;
import com.example.albumsService.service.AlbumsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/users/{userId}/albums")
public class AlbumsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlbumsService albumsService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AlbumResponseModel> getAlbumsByUserId(@PathVariable String userId) {
        List<AlbumResponseModel> listAlbumResponseModel = new ArrayList<>();
        List<AlbumsEntity> listAlbumEntity = albumsService.getAlbumsByUserId(userId);
        if ((listAlbumEntity == null) || listAlbumEntity.isEmpty()) {
            return listAlbumResponseModel;
        }
        Type listType = new TypeToken<List<AlbumResponseModel>>() {}.getType();

        listAlbumResponseModel = new ModelMapper().map(listAlbumEntity, listType);
        logger.info("Returning " + listAlbumResponseModel.size() + " albums.");
        return listAlbumResponseModel;
    }

}