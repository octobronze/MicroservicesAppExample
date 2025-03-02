package com.example.email_service.kafka;

import com.example.email_service.dtos.EmailReceiveDto;
import com.example.email_service.enums.MailServiceMethodNamesEnum;
import com.example.email_service.services.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    private static final String VERIFICATION_LINK_PARAM_NAME = "verificationLink";

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public KafkaService(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    public void produce(String data) throws JsonProcessingException {
        EmailReceiveDto dto = objectMapper.readValue(data, EmailReceiveDto.class);

        MailServiceMethodNamesEnum enm = MailServiceMethodNamesEnum.stringToEnum(dto.getMethodName());

        switch (enm) {
            case SEND_VERIFICATION_LINK -> {
                mailService.sendVerificationLink(dto.getEmail(), (String) dto.getParams().get(VERIFICATION_LINK_PARAM_NAME));
            }
        }
    }
}
