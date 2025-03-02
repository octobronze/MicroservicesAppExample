package com.example.gateway_service.auth.services;

import com.example.gateway_service.auth.dtos.LoginRequestDto;
import com.example.gateway_service.auth.dtos.LoginResponseDto;
import com.example.gateway_service.auth.dtos.UserResponseDto;
import com.example.gateway_service.auth.exceptions.BadRequestException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.gateway_service.auth.consts.ExceptionConsts.INVALID_DATA_EXCEPTION;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EurekaClient eurekaClient;

    @Value("${user.service.id}")
    private String userServiceId;

    @Value("${jwt.duration}")
    private Long jwtDuration;

    private static final String USER_API = "/user";

    public AuthService(JwtService jwtService, PasswordEncoder passwordEncoder, EurekaClient eurekaClient) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.eurekaClient = eurekaClient;
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        UserResponseDto user = getUserFromUserService(dto.getEmail());

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String token = jwtService.generateTokenForUser(user.getId());

            LoginResponseDto responseDto = new LoginResponseDto();

            responseDto.setToken(token);
            responseDto.setDuration(jwtDuration);

            return responseDto;
        }

        throw new BadRequestException(INVALID_DATA_EXCEPTION);
    }

    private UserResponseDto getUserFromUserService(String email) {
        InstanceInfo userService = eurekaClient.getNextServerFromEureka(userServiceId, false);
        String userServiceUrl = userService.getHomePageUrl();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<UserResponseDto> response = restTemplate
                .exchange(userServiceUrl + USER_API + "/" + email, HttpMethod.GET, requestEntity, UserResponseDto.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        }

        throw new BadRequestException(INVALID_DATA_EXCEPTION);
    }
}
