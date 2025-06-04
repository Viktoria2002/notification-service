package ru.aston.notificationservice.kafka.listener.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import ru.aston.kafkadtolibrary.dto.request.ClosingCreditNotificationDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.kafka.filer.DeserializationRecordFilter;
import ru.aston.notificationservice.kafka.listener.KafkaConsumerListener;
import ru.aston.notificationservice.kafka.listener.container.KafkaContainer;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@SpringBootTest(classes = {KafkaConsumerListener.class, MessageTemplateLoader.class,
    SubjectProperties.class, GrpcEmailService.class, KafkaAutoConfiguration.class,
    DeserializationRecordFilter.class})
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {KafkaContainer.class})
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ListenCloseCreditMessagesTest {

  @Value("${spring.kafka.consumer.topics.close-credit-topic}")
  private String creditClosedTopic;

  @Autowired
  private KafkaTemplate<String, ClosingCreditNotificationDto> kafkaTemplate;

  @MockBean
  private GrpcEmailService grpcEmailService;

  @MockBean
  private MessageTemplateLoader templateLoader;

  @SpyBean
  private KafkaConsumerListener kafkaConsumerListener;

  @Captor
  private ArgumentCaptor<List<ConsumerRecord<String, ClosingCreditNotificationDto>>> captor;

  @Test
  void listenCloseCreditMessages_successfullyMessageReceived() {
    String testString = "test";
    List<ClosingCreditNotificationDto> closingCreditNotificationDtos = generateListOfDtos(5);
    closingCreditNotificationDtos
        .forEach(dto -> kafkaTemplate.send(creditClosedTopic, dto));
    kafkaTemplate.flush();

    when(templateLoader.loadCreditCloseTemplatePath(any())).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).listenCloseCreditMessages(captor.capture());

    List<ClosingCreditNotificationDto> capturedDtoList = captor.getValue().stream()
        .map(ConsumerRecord::value)
        .collect(Collectors.toList());

    assertEquals(closingCreditNotificationDtos, capturedDtoList);
  }

  @Test
  void listenCloseCreditMessages_filter() {
    String testString = "test";
    List<ClosingCreditNotificationDto> closingCreditNotificationDtos = generateListOfDtos(2);
    ClosingCreditNotificationDto notificationDto = ClosingCreditNotificationDto.builder()
        .creditName(null)
        .build();
    closingCreditNotificationDtos.add(notificationDto);
    closingCreditNotificationDtos
        .forEach(dto -> kafkaTemplate.send(creditClosedTopic, dto));
    kafkaTemplate.flush();

    when(templateLoader.loadCreditCloseTemplatePath(any())).thenReturn(testString);
    doNothing().when(grpcEmailService).send(any(), any(), any());

    verify(kafkaConsumerListener, timeout(5000)).listenCloseCreditMessages(captor.capture());

    List<ClosingCreditNotificationDto> capturedDtoList = captor.getValue().stream()
        .map(ConsumerRecord::value)
        .collect(Collectors.toList());

    assertEquals(closingCreditNotificationDtos.size() - 1, capturedDtoList.size());
  }


  private List<ClosingCreditNotificationDto> generateListOfDtos(int size) {
    return IntStream.range(0, size)
        .mapToObj(i -> generateClosingCreditNotificationDto())
        .collect(Collectors.toList());
  }

  private ClosingCreditNotificationDto generateClosingCreditNotificationDto() {
    Faker faker = new Faker();
    ClosingCreditNotificationDto dto = new ClosingCreditNotificationDto();
    dto.setEmailTo(faker.internet().emailAddress());
    dto.setCustomerName(faker.name().fullName());
    dto.setCreditName(faker.finance().creditCard());
    dto.setCreditSum(BigDecimal.valueOf(faker.number().randomNumber(5, true)));
    dto.setCreditCloseDateFact(faker.date().future(30, TimeUnit.of(ChronoUnit.DAYS)).toInstant()
        .atZone(ZoneId.systemDefault()).toLocalDateTime());
    return dto;
  }
}
