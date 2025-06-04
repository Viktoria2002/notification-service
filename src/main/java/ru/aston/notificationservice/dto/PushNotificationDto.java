package ru.aston.notificationservice.dto;

import jakarta.validation.constraints.NotNull;

public record PushNotificationDto(@NotNull Boolean hasPushNotification) {

}