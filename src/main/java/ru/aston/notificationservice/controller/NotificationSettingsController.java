package ru.aston.notificationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;

public interface NotificationSettingsController {

    @GetMapping("/all/{notificationProfileId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    NotificationProfileDto getClientNotificationsSettings(@PathVariable String notificationProfileId);
}
