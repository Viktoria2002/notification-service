package ru.aston.notificationservice.service;

import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;

import java.util.UUID;

public interface NotificationSettingsService {

    NotificationProfileDto getNotificationSettings(String notificationProfileId);

    NotificationProfileEntity findNotificationProfileEntityById (String notificationProfileId);
}
