package com.springCloud.userService.service;

import com.springCloud.userService.dto.UserDto;
import com.springCloud.userService.entity.UserEntity;
import com.springCloud.userService.feign.AlbumsFeignClient;
import com.springCloud.userService.model.AlbumResponseModel;
import com.springCloud.userService.repository.UserRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private AlbumsFeignClient albumsFeignClient;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;
    private RestTemplate restTemplate;
    private UserRepository usersRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserServiceImpl(AlbumsFeignClient albumsFeignClient, Environment env,
                           BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate, UserRepository usersRepository) {
        this.albumsFeignClient = albumsFeignClient;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.usersRepository = usersRepository;
    }

    public UserDto create(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        usersRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDto.class);
    }

    //Implement UserDetailsService method to get user details for authentication from dateabse
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserDetailsById(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException(userId);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        // Using RestTemplate

        /*
         * String albumsUrl = String.format(env.getProperty("microservice.url.albums"), userId);
         * ResponseEntity<List<AlbumResponseModel>> listAlbumsResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() { });
         * List<AlbumResponseModel> albums = listAlbumsResponse.getBody();
         */

        // Using Feign Client

        List<AlbumResponseModel> albums = new ArrayList<>();

        try {
            albums = albumsFeignClient.getAlbumsByUserId(userId);
        } catch (FeignException fe) {
            logger.error(fe.getMessage());
        }

        userDto.setAlbums(albums);

        return userDto;
    }

}