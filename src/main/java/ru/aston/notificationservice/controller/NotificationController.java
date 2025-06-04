package ru.aston.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.notificationservice.aspect.Loggable;
import ru.aston.notificationservice.dto.request.NotificationProfileBySmsRequestDto;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;
import ru.aston.notificationservice.service.NotificationProfileService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Loggable
@RequestMapping("/settings")
public class NotificationController {

    private final NotificationProfileService notificationProfileService;

    @PatchMapping("/notifications/sms/{notificationProfileId}")
    public ResponseEntity<NotificationProfileBySmsResponseDto> updateSmsNotification(
            @RequestBody NotificationProfileBySmsRequestDto notificationProfileBySmsRequestDto,
            @PathVariable UUID notificationProfileId) {
        return ResponseEntity.ok(
                notificationProfileService.updateSmsNotification(
                    notificationProfileBySmsRequestDto, notificationProfileId));
    }
}
