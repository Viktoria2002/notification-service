package ru.aston.notificationservice.dto;

import jakarta.validation.constraints.NotNull;

public record EmailNotificationProfileDto(@NotNull Boolean hasEmailSubscription) {
}
