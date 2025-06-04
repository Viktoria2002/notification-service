package ru.aston.notificationservice.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dao.repository.NotificationProfileRepository;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.exception.enum_message.ExceptionMessage;

@ExtendWith(MockitoExtension.class)
public class NotificationSettingsServiceImplTest {

  @Mock
  private NotificationProfileRepository notificationProfileRepository;
  @InjectMocks
  public NotificationSettingsServiceImpl notificationSettingsService;


  @Test
  public void getNotificationSettings_whenNotificationProfileIdIsPassedWhichIsNotInTheDatabase_thenReturnNotFoundNotificationProfileException() {
    UUID notificationProfileIdUuid = UUID.randomUUID();
    String notificationProfileIdString = String.valueOf(notificationProfileIdUuid);

    when(notificationProfileRepository.findById(notificationProfileIdUuid)).thenThrow(
        new NotFoundNotificationProfileException(
            ExceptionMessage.NOTIFICATION_PROFILE_NOT_FOUND + notificationProfileIdString));

    Assertions.assertThrows(NotFoundNotificationProfileException.class,
        () -> notificationSettingsService.getNotificationSettings(notificationProfileIdString));
    verify(notificationProfileRepository).findById(notificationProfileIdUuid);
  }

  @Test
  public void getNotificationSettings_whenNotificationProfileIdIsInTheDatabase_thenSuccessfullyReceivedNotificationSettings() {
    UUID notificationProfileIdUuid = UUID.randomUUID();
    String notificationProfileIdString = String.valueOf(notificationProfileIdUuid);
    NotificationProfileEntity notificationProfileEntity = NotificationProfileEntity.builder()
        .hasPushNotification(true).emailNotification(false).smsNotification(false).build();
    
    when(notificationProfileRepository.findById(notificationProfileIdUuid)).thenReturn(Optional.of(notificationProfileEntity));

    NotificationProfileDto notificationProfileDto = notificationSettingsService.getNotificationSettings(notificationProfileIdString);
    Assertions.assertNotNull(notificationProfileDto);
    Assertions.assertEquals(notificationProfileEntity.getEmailNotification(),notificationProfileDto.getEmailNotification());
    Assertions.assertEquals(notificationProfileEntity.getSmsNotification(),notificationProfileDto.getSmsNotification());
    Assertions.assertEquals(notificationProfileEntity.getHasPushNotification(),notificationProfileDto.getPushNotification());
    verify(notificationProfileRepository).findById(notificationProfileIdUuid);
  }
}
