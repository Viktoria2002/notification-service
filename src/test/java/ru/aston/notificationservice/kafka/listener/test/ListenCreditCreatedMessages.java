package ru.aston.notificationservice.kafka.listener.test;

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
import ru.aston.kafkadtolibrary.dto.request.CreditCreatedKafkaRequestDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.kafka.listener.KafkaConsumerListener;
import ru.aston.notificationservice.kafka.listener.container.KafkaContainer;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@SpringBootTest(classes = {KafkaConsumerListener.class, MessageTemplateLoader.class,
    SubjectProperties.class, GrpcEmailService.class, KafkaAutoConfiguration.class,})
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {KafkaContainer.class})
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ListenCreditCreatedMessages {

  @Value("${spring.kafka.consumer.topics.credit-created-topic}")
  private String creditCreatedTopic;

  @Autowired
  private KafkaTemplate<String, CreditCreatedKafkaRequestDto> kafkaTemplate;

  @MockBean
  private GrpcEmailService grpcEmailService;

  @MockBean
  private SubjectProperties subjectProperties;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @SpyBean
  private KafkaConsumerListener kafkaConsumerListener;

  @Captor
  private ArgumentCaptor<CreditCreatedKafkaRequestDto> captor;


  @Test
  void listenCreditCreatedMessages_successfullyMessageReceived() {
    String testString = "test";
    CreditCreatedKafkaRequestDto creditCreatedKafkaRequestDto = createCreditCreatedKafkaRequestDto();

    kafkaTemplate.send(creditCreatedTopic, creditCreatedKafkaRequestDto);
    kafkaTemplate.flush();

    when(templateLoader.loadCreditCreatedTemplate(any())).thenReturn(testString);
    when(subjectProperties.getCreditCreatedSubject()).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).listenCreditCreatedMessages(captor.capture());

    CreditCreatedKafkaRequestDto messageFromKafka = captor.getValue();

    assertEquals(creditCreatedKafkaRequestDto.emailTo(), messageFromKafka.emailTo());
  }

  private CreditCreatedKafkaRequestDto createCreditCreatedKafkaRequestDto() {
    return CreditCreatedKafkaRequestDto
        .builder()
        .userProfileId(String.valueOf(UUID.randomUUID()))
        .emailTo("test@mail.ru")
        .name("testName")
        .agrNumber("123456")
        .build();
  }
}
