package ru.aston.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.notificationservice.aspect.Loggable;
import ru.aston.notificationservice.dto.PushNotificationDto;
import ru.aston.notificationservice.service.NotificationProfileService;

import java.util.UUID;

@RestController
@RequestMapping("settings/notifications")
@RequiredArgsConstructor
@Loggable
public class SettingsController {

    private final NotificationProfileService notificationProfileService;

    @PatchMapping("/push/{notificationProfileId}")
    public ResponseEntity<PushNotificationDto> updatePushNotification
            (@PathVariable UUID notificationProfileId, @RequestBody PushNotificationDto pushNotificationDto) {
        return ResponseEntity.ok(notificationProfileService.updatePushNotification
                (notificationProfileId, pushNotificationDto));
    }

}