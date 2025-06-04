package ru.aston.notificationservice.service;

import java.util.UUID;
import ru.aston.notificationservice.dto.EmailNotificationProfileDto;
import ru.aston.notificationservice.dto.PushNotificationDto;
import ru.aston.notificationservice.dto.request.NotificationProfileBySmsRequestDto;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;

public interface NotificationProfileService {
  EmailNotificationProfileDto changeEmailNotificationProfile(
      UUID notificationProfileId, EmailNotificationProfileDto emailNotificationProfileDto);

  NotificationProfileBySmsResponseDto updateSmsNotification(
      NotificationProfileBySmsRequestDto notificationProfileBySmsRequestDto,
      UUID notificationProfileId);

  PushNotificationDto updatePushNotification(
      UUID notificationProfileId, PushNotificationDto pushNotificationDto);

}