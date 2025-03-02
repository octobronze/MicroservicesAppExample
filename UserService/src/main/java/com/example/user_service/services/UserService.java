package com.example.user_service.services;

import com.example.user_service.dtos.EmailSendDto;
import com.example.user_service.dtos.UserInfoResponseDto;
import com.example.user_service.dtos.UserProfileResponseDto;
import com.example.user_service.dtos.UserRegistrationRequestDto;
import com.example.user_service.enums.MailServiceMethodNamesEnum;
import com.example.user_service.exceptions.BadRequestException;
import com.example.user_service.kafka.KafkaProducer;
import com.example.user_service.repos.UserRepo;
import com.example.user_service.tables.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.example.user_service.consts.ExceptionConsts.*;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final KafkaProducer kafkaProducer;

    @Value("${gateway.url}")
    private String gatewayUrl;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder,
                       VerificationCodeService verificationCodeService, KafkaProducer kafkaProducer) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeService = verificationCodeService;
        this.kafkaProducer = kafkaProducer;
    }

    public void registerUser(UserRegistrationRequestDto dto) throws JsonProcessingException {
        if (userRepo.existsByEmailAndIsRegisteredTrue(dto.getEmail())) {
            throw new BadRequestException(USER_WITH_EMAIL_EXISTS);
        }
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        User user = userRepo.findByEmail(dto.getEmail()).orElseGet(() -> {
            User newUser = new User();

            newUser.setEmail(dto.getEmail());
            newUser.setRegistered(false);
            newUser.setFirstName(dto.getFirstName());
            newUser.setPassword(encryptedPassword);
            newUser.setLastName(dto.getLastName());

            return newUser;
        });
        userRepo.save(user);

        String code = verificationCodeService.generateCodeForUser(dto.getEmail());
        String verificationLink = generateVerificationLink(dto.getEmail(), code);
        EmailSendDto sendDto = new EmailSendDto();
        sendDto.setMethodName(MailServiceMethodNamesEnum.SEND_VERIFICATION_LINK.getMethodName());
        sendDto.setEmail(dto.getEmail());
        sendDto.setParams(new HashMap<>() {
            {
                put("verificationLink", verificationLink);
            }
        });
        kafkaProducer.sendToEmailTopic(sendDto);
    }

    public void verifyUserRegistration(String email, String code) {
        if (verificationCodeService.checkCodeForUser(email, code)) {
            User user = userRepo.findByEmail(email).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
            user.setRegistered(true);
            userRepo.save(user);

            return;
        }

        throw new BadRequestException(VERIFICATION_ERROR);
    }

    private String generateVerificationLink(String email, String code) {
        return gatewayUrl + "/user/registration/verify/" + email + "/" + code;
    }

    public UserInfoResponseDto getUserInfo(String email) {
        System.out.println(email);
        User user = userRepo.findByEmailAndIsRegisteredTrue(email).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        UserInfoResponseDto response = new UserInfoResponseDto();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());

        return response;
    }

    public UserProfileResponseDto getUserProfile(Integer userId) {
        User user = userRepo.findByIdAndIsRegisteredTrue(userId).orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        return response;
    }
}
