package ru.aston.notificationservice.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.util.backoff.FixedBackOff;
import ru.aston.kafkadtolibrary.dto.request.ClosingCreditNotificationDto;
import ru.aston.notificationservice.kafka.filer.DeserializationRecordFilter;
import ru.aston.notificationservice.kafka.handler.CustomRecordRecoverer;

@Configuration
public class KafkaConfiguration {

  @Value("${kafka.retry.retry-interval-ms}")
  private long retryIntervalMs;

  @Value("${kafka.retry.retry-max-attempts}")
  private long retryMaxAttempts;

  @Bean
  DefaultErrorHandler customDefaultErrorHandler() {
    FixedBackOff fixedBackOff = new FixedBackOff(retryIntervalMs, retryMaxAttempts);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(new CustomRecordRecoverer(), fixedBackOff);
    errorHandler.setLogLevel(KafkaException.Level.TRACE);
    errorHandler.addRetryableExceptions(RuntimeException.class);
    return errorHandler;
  }
}
