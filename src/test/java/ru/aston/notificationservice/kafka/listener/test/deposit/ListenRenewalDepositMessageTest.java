package ru.aston.notificationservice.kafka.listener.test.deposit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositRenewalRequestDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.kafka.listener.DepositServiceKafkaConsumerListener;
import ru.aston.notificationservice.kafka.listener.container.KafkaContainer;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@SpringBootTest(classes = {MessageTemplateLoader.class,
    SubjectProperties.class, GrpcEmailService.class, KafkaAutoConfiguration.class,})
@ContextConfiguration(classes = {KafkaContainer.class})
@TestPropertySource(locations = "classpath:application.yml")
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ListenRenewalDepositMessageTest {

  @Value("${spring.kafka.consumer.topics.renewal-deposit-topic}")
  private String renewalDepositTopic;

  @MockBean
  private GrpcEmailService emailService;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @MockBean
  private SubjectProperties subjects;

  @SpyBean
  private DepositServiceKafkaConsumerListener listener;

  @Captor
  private ArgumentCaptor<DepositRenewalRequestDto> captor;

  @Autowired
  private KafkaTemplate<String, DepositRenewalRequestDto> kafkaTemplate;

  @Test
  void listenRenewalDepositMessage() {
    DepositRenewalRequestDto fakeDto = createFakeDto();
    String test = "test";

    kafkaTemplate.send(renewalDepositTopic, fakeDto);
    kafkaTemplate.flush();

    when(templateLoader.loadDepositRenewalTemplate(fakeDto)).thenReturn(test);
    when(subjects.getRenewalDepositSubject()).thenReturn(test);
    doNothing().when(emailService).send(anyString(), anyString(), anyString());

    verify(listener, timeout(5000)).receiveMessageRenewalDeposit(captor.capture());

    DepositRenewalRequestDto value = captor.getValue();
    assertEquals(fakeDto.emailTo(), value.emailTo());

  }

  private DepositRenewalRequestDto createFakeDto() {
    return DepositRenewalRequestDto.builder()
        .emailTo("test@test.ru")
        .clientName("John Doe")
        .depositProductName("John's deposit")
        .sum("100")
        .build();
  }

}
