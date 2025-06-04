package ru.aston.notificationservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class SubjectProperties {

  private String approveCreditSubject;

  private String creditPaymentDoneSubject;

  private String creditCreatedSubject;

  private String applicationRejectedSubject;

  private String changeAccountStatusSubject;

  private String closeDepositSubject;

  private String renewalDepositSubject;

  private String depositApplicationAcceptedSubject;

  private String createDepositSubject;

}
