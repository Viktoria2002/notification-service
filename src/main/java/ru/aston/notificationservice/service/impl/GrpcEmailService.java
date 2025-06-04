package ru.aston.notificationservice.service.impl;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.aston.EmailVerificationService.EmailVerificationServiceGrpc.EmailVerificationServiceImplBase;
import ru.aston.EmailVerificationService.EmailVerificationServiceOuterClass;
import ru.aston.EmailVerificationService.EmailVerificationServiceOuterClass.verificationRequest;
import ru.aston.EmailVerificationService.EmailVerificationServiceOuterClass.verificationResponse;
import ru.aston.notificationservice.service.MessageService;

@GRpcService
@RequiredArgsConstructor
@Slf4j
public class GrpcEmailService extends EmailVerificationServiceImplBase implements MessageService {

  @Value("${spring.mail.verification-subject}")
  private String verificationSubject;

  @Value("${spring.mail.username}")
  private String emailFrom;

  private final JavaMailSender javaMailSender;

  @Override
  public void verifyEmail(verificationRequest request,
      StreamObserver<verificationResponse> responseObserver) {
    //запускается в отдельном потоке т.к. выполнение блокирует основной поток на >10_000ms
    CompletableFuture.runAsync(
        () -> send(request.getEmail(), buildEmailVerificationMessage(request.getUrl()),
            verificationSubject)
    );

    EmailVerificationServiceOuterClass.verificationResponse response =
        EmailVerificationServiceOuterClass.verificationResponse.newBuilder().setVerified(true)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void send(String to, String message, String subject) {
    SimpleMailMessage email = new SimpleMailMessage();

    email.setFrom(emailFrom);
    email.setTo(to);
    email.setSubject(subject);
    email.setText(message);

    javaMailSender.send(email);
    log.info("Message with subject {} was sent to email {}", subject, email);
  }

  private String buildEmailVerificationMessage(String url) {
    return String.format("Перейдите по ссылке для подтверждения email - %s", url);
  }
}
