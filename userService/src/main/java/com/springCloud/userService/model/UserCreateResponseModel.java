package com.springCloud.userService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponseModel {

    private String firstName;
    private String lastName;
    private String email;
    private String userId;
}
