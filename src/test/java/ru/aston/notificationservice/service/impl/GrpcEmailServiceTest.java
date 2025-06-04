package ru.aston.notificationservice.service.impl;

import static org.grpcmock.GrpcMock.unaryMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.grpcmock.GrpcMock;
import org.grpcmock.junit5.GrpcMockExtension;
import org.grpcmock.springboot.AutoConfigureGrpcMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import ru.aston.EmailVerificationService.EmailVerificationServiceGrpc;
import ru.aston.EmailVerificationService.EmailVerificationServiceGrpc.EmailVerificationServiceBlockingStub;
import ru.aston.EmailVerificationService.EmailVerificationServiceOuterClass.verificationRequest;
import ru.aston.EmailVerificationService.EmailVerificationServiceOuterClass.verificationResponse;

@ExtendWith({MockitoExtension.class, GrpcMockExtension.class})
@AutoConfigureGrpcMock(useInProcessServer = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GrpcEmailServiceTest {

  @Mock
  private JavaMailSender javaMailSender;

  @Mock
  StreamObserver<verificationResponse> observer;

  private static ManagedChannel channel;

  private static EmailVerificationServiceBlockingStub stub;

  @InjectMocks
  private GrpcEmailService grpcEmailService;

  @BeforeAll
  static void setup() {
    channel = ManagedChannelBuilder
        .forAddress("localhost", GrpcMock.getGlobalPort())
        .usePlaintext()
        .build();
    stub = EmailVerificationServiceGrpc.newBlockingStub(channel);
  }

  @AfterAll
  static void shutdownChannel() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  @Test
  void verifyEmail() {
    ReflectionTestUtils.setField(
        grpcEmailService, "verificationSubject", "Подтверждение email"
    );
    ReflectionTestUtils.setField(grpcEmailService, "emailFrom", "testsmrn@gmail.com");
    ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    String email = "test@mail.com";
    String url = "/test";
    verificationRequest request = buildVerificationRequest(email, url);

    GrpcMock.stubFor(unaryMethod(EmailVerificationServiceGrpc.getVerifyEmailMethod())
        .withRequest(request)
        .willReturn(buildResponse(true)));

    verificationResponse response = stub.verifyEmail(request);

    grpcEmailService.verifyEmail(request, observer);

    verify(javaMailSender, timeout(5000)).send(mailCaptor.capture());
    SimpleMailMessage capturedEmail = mailCaptor.getValue();
    verify(observer, times(1)).onNext(response);
    verify(observer, times(1)).onCompleted();
    assertEquals(email, Objects.requireNonNull(capturedEmail.getTo())[0]);
    assertEquals(
        ReflectionTestUtils.getField(grpcEmailService, "verificationSubject"),
        capturedEmail.getSubject()
    );
    assertEquals(
        ReflectionTestUtils.getField(grpcEmailService, "emailFrom"),
        capturedEmail.getFrom()
    );
    assertTrue(response.getVerified());
  }

  private verificationResponse buildResponse(boolean val) {
    return verificationResponse.newBuilder()
        .setVerified(val)
        .build();
  }

  private verificationRequest buildVerificationRequest(String email, String url) {
    return verificationRequest.newBuilder()
        .setEmail(email)
        .setUrl(url)
        .build();
  }
}
