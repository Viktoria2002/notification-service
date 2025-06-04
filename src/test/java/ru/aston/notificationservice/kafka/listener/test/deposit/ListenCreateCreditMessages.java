package ru.aston.notificationservice.kafka.listener.test.deposit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.kafkadtolibrary.dto.request.CreditPaymentDoneKafkaRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.CreatingNewDepositRequestDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.kafka.listener.DepositServiceKafkaConsumerListener;
import ru.aston.notificationservice.kafka.listener.KafkaConsumerListener;
import ru.aston.notificationservice.kafka.listener.container.KafkaContainer;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@SpringBootTest(classes = {DepositServiceKafkaConsumerListener.class, MessageTemplateLoader.class,
    SubjectProperties.class, GrpcEmailService.class, KafkaAutoConfiguration.class,})
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {KafkaContainer.class})
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ListenCreateCreditMessages {

  @Value("${spring.kafka.consumer.topics.create-deposit-topic}")
  private String createDepositTopic;

  @Autowired
  private KafkaTemplate<String, CreatingNewDepositRequestDto> kafkaTemplate;

  @MockBean
  private GrpcEmailService grpcEmailService;

  @MockBean
  private SubjectProperties subjectProperties;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @SpyBean
  private DepositServiceKafkaConsumerListener kafkaConsumerListener;

  @Captor
  private ArgumentCaptor<CreatingNewDepositRequestDto> captor;

  @Test
  void listenCreateCreditMessages_successfullyMessageReceived() {
    String testString = "test";
    CreatingNewDepositRequestDto creatingNewDepositRequestDto = createNewDepositRequestDto();

    kafkaTemplate.send(createDepositTopic, creatingNewDepositRequestDto);
    kafkaTemplate.flush();

    when(templateLoader.loadCreateDepositTemplate(any())).thenReturn(testString);
    when(subjectProperties.getCreateDepositSubject()).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).receiveMessageCreateCredit(captor.capture());

    CreatingNewDepositRequestDto messageFromKafka = captor.getValue();

    assertEquals(creatingNewDepositRequestDto.emailTo(), messageFromKafka.emailTo());
  }

  private CreatingNewDepositRequestDto createNewDepositRequestDto() {
    return CreatingNewDepositRequestDto
        .builder()
        .clientName("Ivan Petrov")
        .depositProductName("New deposit")
        .sum("200000")
        .currency("RUB")
        .emailTo("ivan@mail.ru")
        .build();
  }
}
