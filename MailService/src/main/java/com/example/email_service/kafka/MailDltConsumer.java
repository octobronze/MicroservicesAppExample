package com.example.email_service.kafka;

import com.example.email_service.dtos.VerificationDto;
import com.example.email_service.services.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Слушатель DLT: повторно отправляет письмо после исчерпания коротких ретраев на основном топике.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MailDltConsumer {
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    /**
     * Повторяет отправку письма из DLT.
     *
     * @param data  json сообщения
     * @param exceptionFqcn  класс исходного исключения
     * @param exceptionMessage  текст исходного исключения
     */
    @KafkaListener(
            topics = "${kafka.mail.topic.name.dlt}",
            groupId = "${spring.kafka.consumer.group-id.dlt}",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void replay(
            String data,
            @Nullable @Header(name = KafkaHeaders.DLT_EXCEPTION_FQCN, required = false) String exceptionFqcn,
            @Nullable @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String exceptionMessage
    ) throws JsonProcessingException {
        log.warn("Replaying verification email from DLT, originalException={}, message={}", exceptionFqcn, exceptionMessage);
        var dto = objectMapper.readValue(data, VerificationDto.class);
        mailService.sendVerificationLink(dto.email(), dto.verificationLink());
    }
}
