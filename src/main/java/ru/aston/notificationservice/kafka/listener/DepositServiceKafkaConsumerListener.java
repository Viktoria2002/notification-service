package ru.aston.notificationservice.kafka.listener;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.kafkadtolibrary.dto.request.deposit.CreatingNewDepositRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositApplicationAcceptedRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositClosedRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositRenewalRequestDto;
import ru.aston.notificationservice.configuration.SubjectProperties;
import ru.aston.notificationservice.service.impl.GrpcEmailService;
import ru.aston.notificationservice.util.MessageTemplateLoader;

@Slf4j
@RequiredArgsConstructor
@Component
public class DepositServiceKafkaConsumerListener {

  private final GrpcEmailService grpcEmailService;

  private final MessageTemplateLoader templateLoader;

  private final SubjectProperties subjects;

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.close-deposit-topic}",
      groupId = "${kafka.closeDepositListener.group-id}",
      autoStartup = "${kafka.closeDepositListener.enabled}",
      properties = "${kafka.closeDepositListener.properties}"
  )
  public void receiveMessageCloseDeposit(@Valid DepositClosedRequestDto request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadDepositClosedTemplate(request);
    sendEmailNotificationClosedDeposit(request, message);
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.renewal-deposit-topic}",
      groupId = "${kafka.renewalDepositListener.group-id}",
      autoStartup = "${kafka.renewalDepositListener.enabled}",
      properties = "${kafka.renewalDepositListener.properties}"
  )
  public void receiveMessageRenewalDeposit(@Valid DepositRenewalRequestDto request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadDepositRenewalTemplate(request);
    sendEmailNotificationRenewalDeposit(request, message);
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.deposit-application-accepted-topic}",
      groupId = "${kafka.depositApplicationAcceptedListener.group-id}",
      autoStartup = "${kafka.depositApplicationAcceptedListener.enabled}",
      properties = "${kafka.depositApplicationAcceptedListener.properties}"
  )
  public void receiveApplicationAcceptedNotification(@Valid DepositApplicationAcceptedRequestDto request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadDepositApplicationAcceptedTemplate(request);
    sendApplicationAcceptedNotification(request, message);
  }

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.create-deposit-topic}",
      groupId = "${kafka.createDepositListener.group-id}",
      autoStartup = "${kafka.listener1.enabled}",
      properties = "${kafka.listener2.properties}"
  )
  public void receiveMessageCreateCredit(@Valid CreatingNewDepositRequestDto request) {
    log.info("Received message: {}", request.toString());
    String message = templateLoader.loadCreateDepositTemplate(request);
    sendEmailNotificationCreateDeposit(request, message);
  }

  private void sendEmailNotificationClosedDeposit(DepositClosedRequestDto request, String message) {
    grpcEmailService.send(request.emailTo(), message, subjects.getCloseDepositSubject());
  }

  private void sendEmailNotificationRenewalDeposit(DepositRenewalRequestDto request, String message) {
    grpcEmailService.send(request.emailTo(), message, subjects.getRenewalDepositSubject());
  }

  private void sendEmailNotificationCreateDeposit(CreatingNewDepositRequestDto request, String message) {
    grpcEmailService.send(request.emailTo(), message, subjects.getCreateDepositSubject());
  }

  private void sendApplicationAcceptedNotification(DepositApplicationAcceptedRequestDto requst,
      String message) {
    grpcEmailService.send(requst.emailTo(), message, subjects.getApplicationRejectedSubject());
  }

}
