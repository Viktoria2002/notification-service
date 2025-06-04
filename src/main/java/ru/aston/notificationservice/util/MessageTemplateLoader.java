package ru.aston.notificationservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import ru.aston.kafkadtolibrary.dto.request.ChangeAccountStatusRequestDto;
import ru.aston.kafkadtolibrary.dto.request.ClosingCreditNotificationDto;
import ru.aston.kafkadtolibrary.dto.request.CreditCreatedKafkaRequestDto;
import ru.aston.kafkadtolibrary.dto.request.CreditNotificationRequest;
import ru.aston.kafkadtolibrary.dto.request.CreditPaymentDoneKafkaRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.CreatingNewDepositRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositClosedRequestDto;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositApplicationAcceptedRequestDto;
import ru.aston.notificationservice.exception.MessageSendException;
import ru.aston.kafkadtolibrary.dto.request.deposit.DepositRenewalRequestDto;

@Slf4j
@Component
public final class MessageTemplateLoader {

  @Value("${classpath:message-template/credit-approve-template.txt}")
  private String creditApproveTemplatePath;

  @Value("${classpath:message-template/credit-payment-done-template.txt}")
  private String creditPaymentDoneTemplatePath;

  @Value("${classpath:message-template/application-withdrawn-by-client-template.txt}")
  private String applicationWithdrawnByClientTemplatePath;

  @Value("${classpath:message-template/credit-created-template.txt}")
  private String creditCreatedTemplatePath;

  @Value("${classpath:message-template/application-rejected-template.txt}")
  private String applicationRejectedTemplatePath;

  @Value("${classpath:message-template/change-account-status-template.txt}")
  private String changeAccountStatusTemplatePath;

  @Value("${classpath:message-template/credit-closed-template.txt}")
  private String closeCreditTemplatePath;

  @Value("${classpath:message-template/deposit/deposit-closed-template.txt}")
  private String depositClosedTemplatePath;

  @Value("${classpath:message-template/deposit/deposit-renewal-template.txt}")
  private String depositRenewalTemplatePath;

  @Value("${classpath:message-template/deposit/deposit-application-accepted-template.txt}")
  private String depositApplicationAcceptedTemplatePath;

  @Value("${classpath:message-template/deposit/deposit-created-template.txt}")
  private String depositCreatedTemplatePath;

  public String loadCreditCloseTemplatePath(ClosingCreditNotificationDto notificationDto) {
    StringBuilder modifiedTemplate = getModifiedTemplate(closeCreditTemplatePath);

    replacePlaceholder(modifiedTemplate, "?customerName?", notificationDto.getCustomerName());
    replacePlaceholder(modifiedTemplate, "?creditName?", notificationDto.getCreditName());
    replacePlaceholder(modifiedTemplate, "?creditSum?",
        String.valueOf(notificationDto.getCreditSum()));
    replacePlaceholder(modifiedTemplate, "?date?",
        String.valueOf(notificationDto.getCreditCloseDateFact().toLocalDate()));

    return modifiedTemplate.toString();
  }

  public String loadCreditApproveTemplate(CreditNotificationRequest request) {
    StringBuilder templateBuilder = getModifiedTemplate(creditApproveTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.name());
    replacePlaceholder(templateBuilder, "?sum?", request.sum());
    replacePlaceholder(templateBuilder, "?percent?", request.percent());
    replacePlaceholder(templateBuilder, "?term?", request.term());

    return templateBuilder.toString();
  }

  public String loadChangeAccountStatusTemplate(ChangeAccountStatusRequestDto request) {
    StringBuilder templateBuilder = getModifiedTemplate(changeAccountStatusTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.name());
    replacePlaceholder(templateBuilder, "?accountName?", request.accountName());
    replacePlaceholder(templateBuilder, "?status?", request.status());

    return templateBuilder.toString();
  }

  public String loadCreditPaymentDoneTemplate(CreditPaymentDoneKafkaRequestDto request) {
    StringBuilder templateBuilder = getModifiedTemplate(creditPaymentDoneTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.name());
    replacePlaceholder(templateBuilder, "?agrNumber?", request.agrNumber());
    replacePlaceholder(templateBuilder, "?date?", request.date());
    replacePlaceholder(templateBuilder, "?paymentSum?", request.sum());

    return templateBuilder.toString();
  }

  public String loadApplicationWithdrawnByClientTemplate(CreditNotificationRequest request) {
    StringBuilder modifiedTemplate = getModifiedTemplate(applicationWithdrawnByClientTemplatePath);

    replacePlaceholder(modifiedTemplate, "?customer?", request.name());
    replacePlaceholder(modifiedTemplate, "?sum?", request.sum());
    replacePlaceholder(modifiedTemplate, "?percent?", request.percent());
    replacePlaceholder(modifiedTemplate, "?term?", request.term());

    return modifiedTemplate.toString();
  }

  public String loadCreditCreatedTemplate(CreditCreatedKafkaRequestDto request) {
    StringBuilder templateBuilder = getModifiedTemplate(creditCreatedTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.name());
    replacePlaceholder(templateBuilder, "?agrNumber?", request.agrNumber());

    return templateBuilder.toString();
  }

  public String loadApplicationRejectedTemplate(CreditNotificationRequest request) {
    StringBuilder templateBuilder = getModifiedTemplate(applicationRejectedTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.name());
    replacePlaceholder(templateBuilder, "?sum?", request.sum());
    replacePlaceholder(templateBuilder, "?percent?", request.percent());
    replacePlaceholder(templateBuilder, "?term?", request.term());

    return templateBuilder.toString();
  }

  public String loadDepositClosedTemplate(DepositClosedRequestDto request) {
    log.info("Attempting to load deposit closed template for DepositClosedRequestDto with email {}", request.emailTo());
    StringBuilder templateBuilder = getModifiedTemplate(depositClosedTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.clientName());
    replacePlaceholder(templateBuilder, "?depositProductName?", request.depositProductName());
    replacePlaceholder(templateBuilder, "?sum?", request.sum());

    log.info("Deposit closed template for DepositClosedRequestDto with email {} was built", request.emailTo());
    return templateBuilder.toString();
  }

  public String loadDepositRenewalTemplate(DepositRenewalRequestDto request) {
    log.info("Attempting to load deposit renewal template for DepositRenewalRequestDto with email {}", request.emailTo());
    StringBuilder templateBuilder = getModifiedTemplate(depositRenewalTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.clientName());
    replacePlaceholder(templateBuilder, "?depositProductName?", request.depositProductName());
    replacePlaceholder(templateBuilder, "?sum?", request.sum());
    log.info("Deposit renewal template for DepositRenewalRequestDto with email {} was built", request.emailTo());
    return templateBuilder.toString();
  }

  public String loadCreateDepositTemplate(CreatingNewDepositRequestDto request) {
    log.info("Attempting to load creating deposit template for CreatingNewDepositRequestDto with email {}", request.emailTo());
    StringBuilder templateBuilder = getModifiedTemplate(depositCreatedTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.clientName());
    replacePlaceholder(templateBuilder, "?depositProductName?", request.depositProductName());
    replacePlaceholder(templateBuilder, "?sum?", request.sum());
    replacePlaceholder(templateBuilder, "?currency?", request.currency());
    log.info("Creating deposit template for CreatingNewDepositRequestDto with email {} was built", request.emailTo());
    return templateBuilder.toString();
  }

  public String loadDepositApplicationAcceptedTemplate(DepositApplicationAcceptedRequestDto request){
    log.info(
        "Attempting to load deposit application accepted template for "
            + "DepositApplicationAcceptedRequestDto with email {}",
        request.emailTo()
    );
    StringBuilder templateBuilder = getModifiedTemplate(depositApplicationAcceptedTemplatePath);

    replacePlaceholder(templateBuilder, "?customer?", request.clientName());
    replacePlaceholder(templateBuilder, "?depositProductName?", request.depositProductName());

    log.info(
        "Deposit closed template for DepositClosedRequestDto with email {} was built",
        request.emailTo()
    );
    return templateBuilder.toString();
  }

  private StringBuilder getModifiedTemplate(String templatePath) throws MessageSendException {
    Resource resource = new ClassPathResource(templatePath);
    try (InputStream resourceAsStream = resource.getInputStream()) {
      byte[] resourceAsByteArray = FileCopyUtils.copyToByteArray(resourceAsStream);
      String template = new String(resourceAsByteArray, StandardCharsets.UTF_8);

      return new StringBuilder(template);
    } catch (IOException e) {
      log.error("Failed to load template from file: {}", templatePath);
      throw new MessageSendException(
          "Failed to load template from file: + " + templatePath + "\n"
              + e.getMessage());
    }
  }

  private void replacePlaceholder(StringBuilder builder, String placeholder, String value) {
    if(builder == null || placeholder == null || value == null){
      return;
    }
    int index = builder.indexOf(placeholder);
    while (index != -1) {
      builder.replace(index, index + placeholder.length(), value);
      index = builder.indexOf(placeholder, index + value.length());
    }
  }

}