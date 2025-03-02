package com.example.user_service.controllers;

import com.example.user_service.dtos.UserInfoResponseDto;
import com.example.user_service.dtos.UserProfileResponseDto;
import com.example.user_service.dtos.UserRegistrationRequestDto;
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequestDto dto) throws JsonProcessingException {
        userService.registerUser(dto);

        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/registration/verify/{email}/{code}")
    public ResponseEntity<String> verifyRegistration(@PathVariable(name = "email") String email,
                                                     @PathVariable(name = "code") String code) {
        userService.verifyUserRegistration(email, code);

        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(userService.getUserInfo(email));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestHeader(name = "userId") Integer userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
