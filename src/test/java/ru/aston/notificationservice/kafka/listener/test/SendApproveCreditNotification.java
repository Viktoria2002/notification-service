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
import ru.aston.kafkadtolibrary.dto.request.CreditNotificationRequest;
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
class SendApproveCreditNotification {

  @Value("${spring.kafka.consumer.topics.approve-credit-topic}")
  private String approveCreditTopic;

  @Autowired
  private KafkaTemplate<String, CreditNotificationRequest> kafkaTemplate;

  @MockBean
  private GrpcEmailService grpcEmailService;

  @MockBean
  private SubjectProperties subjectProperties;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @SpyBean
  private KafkaConsumerListener kafkaConsumerListener;

  @Captor
  private ArgumentCaptor<CreditNotificationRequest> captor;

  @Test
  void sendApproveCreditNotification_successfullyMessageReceived() {
    String testString = "test";
    CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

    kafkaTemplate.send(approveCreditTopic, creditNotificationRequest);
    kafkaTemplate.flush();

    when(templateLoader.loadCreditApproveTemplate(any())).thenReturn(testString);
    when(subjectProperties.getApproveCreditSubject()).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).sendApproveCreditNotification(captor.capture());

    CreditNotificationRequest messageFromKafka = captor.getValue();

    assertEquals(creditNotificationRequest.emailTo(), messageFromKafka.emailTo());
  }

  private CreditNotificationRequest createCreditNotificationRequest() {
    return new CreditNotificationRequest(
        "test@mail.ru",
        "testName",
        "100",
        "5",
        "5"
    );
  }
}

