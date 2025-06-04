package ru.aston.notificationservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import ru.aston.notificationservice.dto.PushNotificationDto;
import ru.aston.notificationservice.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class NotificationProfileServiceImplMethodUpdatePushNotificationTest {
  @Mock
  private NotificationProfileRepository notificationProfileRepository;

  @InjectMocks
  private NotificationProfileServiceImpl notificationProfileService;

  @Test
  void updatePushNotification_whenNotificationProfileIdIsInTheDatabase_thenPushNotificationIsSuccessfullyUpdated(){
    //given
    UUID notificationProfileId = UUID.randomUUID();
    PushNotificationDto pushNotificationDto = new PushNotificationDto(true);
    NotificationProfileEntity notificationProfileEntity = NotificationProfileEntity.builder().id(notificationProfileId).hasPushNotification(false).build();

    //when
    when(notificationProfileRepository.findById(notificationProfileId)).thenReturn(Optional.of(notificationProfileEntity));
    notificationProfileService.updatePushNotification(notificationProfileId,pushNotificationDto);

    //then
    verify(notificationProfileRepository).save(any());
    Assertions.assertEquals(pushNotificationDto.hasPushNotification(), notificationProfileEntity.getHasPushNotification());
  }
  @Test
  void UpdatePushNotification_whenNotificationProfileIdIsPassedWhichIsNotInTheDatabase_thenReturnEntityNotFoundException() {
    //given
    UUID notificationProfileId = UUID.randomUUID();
    PushNotificationDto pushNotificationDto = new PushNotificationDto(true);

    //when
    when(notificationProfileRepository.findById(notificationProfileId)).thenThrow(new EntityNotFoundException(notificationProfileId,
        NotificationProfileEntity.class));

    //then
    Assertions.assertThrows(EntityNotFoundException.class,
        () -> notificationProfileService.updatePushNotification(notificationProfileId,pushNotificationDto));
    verify(notificationProfileRepository,never()).save(any());
  }
}
