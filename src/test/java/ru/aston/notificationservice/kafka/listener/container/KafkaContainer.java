package ru.aston.notificationservice.kafka.listener.container;


import org.springframework.context.annotation.Configuration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class KafkaContainer {

  @Container
  static org.testcontainers.containers.KafkaContainer kafkaContainer = new org.testcontainers.containers.KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));


  static {
    kafkaContainer.start();
    System.setProperty("spring.kafka.consumer.bootstrap-servers", kafkaContainer.getBootstrapServers());
    System.setProperty("spring.kafka.producer.bootstrap-servers", kafkaContainer.getBootstrapServers());
  }
}