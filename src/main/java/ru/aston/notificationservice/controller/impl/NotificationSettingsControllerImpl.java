package ru.aston.notificationservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.notificationservice.aspect.Loggable;
import ru.aston.notificationservice.controller.NotificationSettingsController;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;
import ru.aston.notificationservice.service.NotificationSettingsService;
@Slf4j
@RestController
@RequiredArgsConstructor
@Loggable
@RequestMapping("/settings/notifications")
public class NotificationSettingsControllerImpl implements NotificationSettingsController {
    private final NotificationSettingsService notificationSettingsService;
    @Override
    public NotificationProfileDto getClientNotificationsSettings(String notificationProfileId) {
      return notificationSettingsService.getNotificationSettings(notificationProfileId);
    }
}
