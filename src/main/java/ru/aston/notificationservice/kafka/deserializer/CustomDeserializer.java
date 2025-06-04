package ru.aston.notificationservice.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.validation.annotation.Validated;
import ru.aston.kafkadtolibrary.dto.request.ClosingCreditNotificationDto;
import ru.aston.notificationservice.kafka.config.ObjectMapperConfig;

@Slf4j
@Validated
public class CustomDeserializer implements Deserializer<ClosingCreditNotificationDto> {

  private final ObjectMapper customObjectMapper;

  public CustomDeserializer() {
    this.customObjectMapper = new ObjectMapperConfig().customObjectMapper();
  }

  public CustomDeserializer(ObjectMapper customObjectMapper) {
    this.customObjectMapper = customObjectMapper;
  }


  @Override
  public ClosingCreditNotificationDto deserialize(String s, byte[] bytes) {
    return null;
  }

  @Override
  public ClosingCreditNotificationDto deserialize(String topic, Headers headers, byte[] data) {
    try {
      if (data == null) {
        log.error("Null received at deserializing");
        return null;
      }
      ClosingCreditNotificationDto notificationDto = customObjectMapper.readValue(
          new String(data, StandardCharsets.UTF_8),
          ClosingCreditNotificationDto.class);
      validateMessage(notificationDto);
      return notificationDto;
    } catch (Exception e) {
      throw new DeserializationException(e.getMessage(), data, false, e);
    }
  }

  private void validateMessage(@Valid ClosingCreditNotificationDto yourMessage) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<ClosingCreditNotificationDto>> violations = validator.validate(
        yourMessage);
    if (!violations.isEmpty()) {
      throw new ValidationException("Message validation failed: " + violations);
    }
  }
}
