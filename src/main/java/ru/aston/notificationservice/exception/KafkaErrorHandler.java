package ru.aston.notificationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaErrorHandler implements CommonErrorHandler {

  @Override
  public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record,
      Consumer<?, ?> consumer, MessageListenerContainer container) {
    log.error("Listener exception in topic [{}] with value {} at offset [{}]. Cause: {}",
        record.topic(), record.value(), record.offset(), thrownException.getCause().getLocalizedMessage());

    if (thrownException.getCause() instanceof DeserializationException) {
      log.error("Error during deserialization. Skipping message.");
    } else {
      log.error("Unexpected error. Returning message for further processing.");
    }

    return true;
  }
}
