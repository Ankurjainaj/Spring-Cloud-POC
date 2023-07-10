package com.springCloud.userService.feign;

import com.springCloud.userService.model.AlbumResponseModel;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "ALBUMS-MICROSERVICE", fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsFeignClient {

    @GetMapping("/users/{userId}/albums")
    public List<AlbumResponseModel> getAlbumsByUserId(@PathVariable String userId);

}

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsFeignClient> {

    @Override
    public AlbumsServiceClientFallback create(Throwable cause) {
        return new AlbumsServiceClientFallback(cause);
    }

}

class AlbumsServiceClientFallback implements AlbumsFeignClient {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Throwable cause;

    public AlbumsServiceClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public List<AlbumResponseModel> getAlbumsByUserId(String id) {

        if (cause instanceof FeignException && ((FeignException) cause).status() == 404) {
            logger.error("404 error took place when getAlbums was called with userId: {}. Error message: {}", id, cause.getLocalizedMessage());
        } else {
            logger.error("Other error took place: {}", cause.getLocalizedMessage());
        }

        return new ArrayList<>();
    }

}