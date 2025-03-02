package com.example.email_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.util.backoff.FixedBackOff;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;

/**
 * Конфигурация Kafka consumer.
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {
    private static final long BACKOFF_INTERVAL_MS = 5000L;
    private static final int MAX_ATTEMPTS = 3;
    private static final long DLT_BACKOFF_INTERVAL_MS = 30_000L;
    private static final int DLT_MAX_ATTEMPTS = 5;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaUrl;
    @Value("${kafka.mail.topic.name.dlt}")
    private String mailTopicDlt;

    /**
     * Создает фабрику consumer.
     *
     * @return фабрика consumer
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Создает обработчик ошибок основного топика.
     *
     * @param kafkaTemplate  шаблон kafka
     * @return обработчик ошибок
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, __) -> new TopicPartition(mailTopicDlt, record.partition())
        );
        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(BACKOFF_INTERVAL_MS, MAX_ATTEMPTS));
        configureExceptionClassification(errorHandler);
        return errorHandler;
    }

    /**
     * Создает фабрику контейнеров слушателей основного топика.
     *
     * @param errorHandler  обработчик ошибок
     * @return фабрика контейнеров слушателей
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Создает фабрику контейнеров слушателей DLT без публикации в следующий DLT.
     *
     * @return фабрика контейнеров слушателей DLT
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> dltKafkaListenerContainerFactory() {
        // DLT не публикуем дальше: poison остаётся здесь, временные сбои SMTP ретраим с большей паузой.
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        var errorHandler = new DefaultErrorHandler(
                (record, exception) -> log.error(
                        "Giving up DLT replay for verification email, topic={}, offset={}",
                        record.topic(),
                        record.offset(),
                        exception
                ),
                new FixedBackOff(DLT_BACKOFF_INTERVAL_MS, DLT_MAX_ATTEMPTS)
        );
        configureExceptionClassification(errorHandler);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    private void configureExceptionClassification(DefaultErrorHandler errorHandler) {
        // Не ретраим poison JSON и постоянные SMTP-ошибки: повтор только блокирует партицию.
        errorHandler.addNotRetryableExceptions(
                JsonProcessingException.class,
                MailAuthenticationException.class,
                MailParseException.class,
                MailPreparationException.class
        );
    }
}
