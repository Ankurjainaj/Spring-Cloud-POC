package com.springCloud.userService.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springCloud.userService.dto.UserDto;
import com.springCloud.userService.model.LoginRequestModel;
import com.springCloud.userService.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService usersService;

    private Environment environment;

    public AuthenticationFilter(UserService usersService, Environment environment,
                                AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl(environment.getProperty("login.url.path"));
    }

    //Will be triggered when the user tries to login through the login endpoint.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel loginRequestModel = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);

            //The authentication will be done through the loadUserByUsername method in UserDetailsService method.
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestModel.getEmail(), loginRequestModel.getPassword(), new ArrayList<>()));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    //Called by spring framework once the authentication is successful and will generate a jwt token using the user details and add it to the header
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
        String userName = ((User) authentication.getPrincipal()).getUsername();

        UserDto userDetails = usersService.getUserDetailsByEmail(userName);

        String authToken = Jwts.builder().setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
                .compact();

        response.addHeader("token", authToken);
        response.addHeader("userId", userDetails.getUserId());
    }
}