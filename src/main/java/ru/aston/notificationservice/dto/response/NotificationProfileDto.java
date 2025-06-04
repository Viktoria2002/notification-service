package ru.aston.notificationservice.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class NotificationProfileDto {

    private Boolean smsNotification;

    private Boolean pushNotification;

    private Boolean emailNotification;
}
