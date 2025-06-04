package ru.aston.notificationservice.kafka.listener;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.kafkadtolibrary.dto.request.ChangeAccountStatusRequestDto;
import ru.aston.kafkadtolibrary.dto.request.ClosingCreditNotificationDto;
import ru.aston.kafkadtolibrary.dto.request.CreditCreatedKafkaRequestDto;
import ru.aston.kafkadtolibrary.dto.request.CreditNotificationRequest;
import ru.aston.kafkadtolibrary.dto.request.CreditPaymentDoneKafkaRequestDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumerListener {

  private final GrpcEmailService emailService;

  private final MessageTemplateLoader templateLoader;

  private final SubjectProperties subjects;

  @Value("${spring.mail.close-credit-subject}")
  private String closeCreditSubject;

  @Value("${spring.mail.credit-payment-done}")
  private String creditPaymentDone;

  @Value("${spring.mail.application-withdrawn}")
  private String withdrawnByClientSubject;

  @Value("${spring.mail.credit-created}")
  private String creditCreated;

  @Value("${spring.mail.application-rejected}")
  private String applicationRejected;

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.close-credit-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener1.properties}",
      batch = "true")
  public void listenCloseCreditMessages(
      List<ConsumerRecord<String, ClosingCreditNotificationDto>> records) {
    log.info("Received [{}] messages", records.size());
    for (ConsumerRecord<String, ClosingCreditNotificationDto> record : records) {
      log.info("Received message: {}", record.value());
      String message = templateLoader.loadCreditCloseTemplatePath(record.value());
      emailService.send(record.value().getEmailTo(), message, closeCreditSubject);
    }
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.approve-credit-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener2.properties}"
  )
  public void sendApproveCreditNotification(@Valid CreditNotificationRequest request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadCreditApproveTemplate(request);
    emailService.send(request.emailTo(), message, subjects.getApproveCreditSubject());
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.change-account-status-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener3.properties}"
  )
  public void sendChangeAccountStatusNotification(@Valid ChangeAccountStatusRequestDto request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadChangeAccountStatusTemplate(request);
    emailService.send(request.emailTo(), message, subjects.getChangeAccountStatusSubject());
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.credit-payment-done-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener4.properties}"
  )
  public void listenCreditPaymentDoneMessages(@Valid CreditPaymentDoneKafkaRequestDto request) {
    log.info("Received message about done credit payment");
    String message = templateLoader.loadCreditPaymentDoneTemplate(request);
    emailService.send(request.emailTo(), message, subjects.getCreditPaymentDoneSubject());
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.application-withdrawn-by-client-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener5.properties}"
  )
  public void sendWithdrawnByClientNotification(@Valid CreditNotificationRequest request) {
    log.info("Received message about withdrawal of application");
    String message = templateLoader.loadApplicationWithdrawnByClientTemplate(request);

    emailService.send(request.emailTo(), message, withdrawnByClientSubject);
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.credit-created-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener6.properties}"
  )
  public void listenCreditCreatedMessages(@Valid CreditCreatedKafkaRequestDto request) {
    log.info("Received message about done credit payment");
    String message = templateLoader.loadCreditCreatedTemplate(request);
    emailService.send(request.emailTo(), message, subjects.getCreditCreatedSubject());
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.application-rejected-topic}",
      groupId = "${spring.kafka.consumer.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener7.properties}"
  )
  public void listenApplicationRejectedMessages(@Valid CreditNotificationRequest request) {
    log.info("Received message about changing notification status for email {}", request.emailTo());
    String message = templateLoader.loadApplicationRejectedTemplate(request);
    emailService.send(request.emailTo(), message, subjects.getApplicationRejectedSubject());
  }
}
