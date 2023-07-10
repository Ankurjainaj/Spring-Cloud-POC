package com.springCloud.userService.service;

import com.springCloud.userService.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto create(UserDto userDto);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserDetailsById(String userId);

}