package com.example.discoveryServer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()     //Disable Cross site request forgery as we don't use csrf token & thus to enable other services to register on eureka
                .disable()
                .authorizeRequests()
                .anyRequest().authenticated()       //Any request needs to be authenticated
                .and()
                .httpBasic();           //Allow user to authenticate using http basic and configure it
    }

}
