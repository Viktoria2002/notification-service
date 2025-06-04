package ru.aston.notificationservice.util;

import java.util.UUID;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dto.request.NotificationProfileBySmsRequestDto;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;

public class Fixtures {

  public static NotificationProfileBySmsRequestDto getNotificationProfileBySmsRequestDto() {
    return NotificationProfileBySmsRequestDto.builder()
        .smsNotification(true)
        .build();
  }
  public static NotificationProfileEntity getNotificationProfileEntity() {
    return NotificationProfileEntity.builder()
        .id(UUID.randomUUID())
        .smsNotification(false)
        .emailNotification(false)
        .hasPushNotification(true)
        .build();
  }

  public static NotificationProfileEntity getNotificationProfileEntityWithWrongId() {
    return NotificationProfileEntity.builder()
        .id(UUID.randomUUID())
        .smsNotification(false)
        .emailNotification(false)
        .hasPushNotification(true)
        .build();
  }

  public static NotificationProfileBySmsResponseDto notificationProfileBySmsResponseDto() {
    return NotificationProfileBySmsResponseDto.builder()
        .smsNotification(true)
        .build();
  }
}
