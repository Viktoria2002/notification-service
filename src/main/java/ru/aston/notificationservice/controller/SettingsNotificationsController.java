package ru.aston.notificationservice.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.notificationservice.aspect.Loggable;
import ru.aston.notificationservice.dto.EmailNotificationProfileDto;
import ru.aston.notificationservice.service.NotificationProfileService;

@RestController
@RequiredArgsConstructor
@Loggable
@Validated
@RequestMapping("/settings/notifications")
public class SettingsNotificationsController {

  private final NotificationProfileService notificationProfileService;

  @PatchMapping("/email/{notificationProfileId}")
  ResponseEntity<EmailNotificationProfileDto> changeEmailNewsletters
      (@PathVariable UUID notificationProfileId,
          @Valid @RequestBody EmailNotificationProfileDto emailNotificationProfileDto) {
    return ResponseEntity.ok(
        notificationProfileService.changeEmailNotificationProfile(
            notificationProfileId, emailNotificationProfileDto));
  }

}
