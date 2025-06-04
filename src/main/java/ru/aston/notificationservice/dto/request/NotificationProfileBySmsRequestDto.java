package ru.aston.notificationservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record NotificationProfileBySmsRequestDto(
    @NotNull(message = "Значение не может быть пустым.") Boolean smsNotification) {

}
