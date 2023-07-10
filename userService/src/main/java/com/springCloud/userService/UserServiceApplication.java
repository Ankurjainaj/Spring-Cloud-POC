package com.springCloud.userService;

import com.springCloud.userService.feign.FeignErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
//@EnableCircuitBreaker
@Slf4j
public class UserServiceApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public Logger.Level getFeignLoggerLevel() {
//        return Logger.Level.FULL;
//    }

    @Bean
    @Profile("production")
    Logger.Level feignLoggerLevel() {
        return Logger.Level.NONE;
    }

    @Bean
    @Profile("!production")
    Logger.Level feignDefaultLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Profile("production")
    public String createProductionBean() {
        log.info("Production bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Production bean";
    }

    @Bean
    @Profile("!production")
    public String createNotProductionBean() {
        log.info("Not Production bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Not production bean";
    }

    @Bean
    @Profile("default")
    public String createDevelopmentBean() {
        log.info("Development bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Development bean";
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder(environment);
    }

}
