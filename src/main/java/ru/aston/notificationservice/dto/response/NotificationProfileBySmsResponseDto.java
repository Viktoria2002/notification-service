package ru.aston.notificationservice.dto.response;

import lombok.Builder;

@Builder
public record NotificationProfileBySmsResponseDto(Boolean smsNotification) {
}
