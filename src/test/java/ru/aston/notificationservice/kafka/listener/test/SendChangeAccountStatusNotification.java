package ru.aston.notificationservice.kafka.listener.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import ru.aston.kafkadtolibrary.dto.request.ChangeAccountStatusRequestDto;
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
class SendChangeAccountStatusNotification {


  @Value("${spring.kafka.consumer.topics.change-account-status-topic}")
  private String approveStatusTopic;

  @Autowired
  private KafkaTemplate<String, ChangeAccountStatusRequestDto> kafkaTemplate;

  @MockBean
  private GrpcEmailService grpcEmailService;

  @MockBean
  private SubjectProperties subjectProperties;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @SpyBean
  private KafkaConsumerListener kafkaConsumerListener;

  @Captor
  private ArgumentCaptor<ChangeAccountStatusRequestDto> captor;

  @Test
  void sendChangeAccountStatusNotification_successfullyMessageReceived() {
    String testString = "test";
    ChangeAccountStatusRequestDto changeAccountStatusRequestDto = createChangeAccountStatusRequestDto();

    kafkaTemplate.send(approveStatusTopic, changeAccountStatusRequestDto);
    kafkaTemplate.flush();

    when(templateLoader.loadChangeAccountStatusTemplate(any())).thenReturn(testString);
    when(subjectProperties.getChangeAccountStatusSubject()).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).sendChangeAccountStatusNotification(
        captor.capture());

    ChangeAccountStatusRequestDto messageFromKafka = captor.getValue();

    assertEquals(changeAccountStatusRequestDto.emailTo(), messageFromKafka.emailTo());
  }

  private ChangeAccountStatusRequestDto createChangeAccountStatusRequestDto() {
    return new ChangeAccountStatusRequestDto(
        "test@mail.ru",
        "testName",
        "testStatus",
        "testAccountName"
    );
  }
}
