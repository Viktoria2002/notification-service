package ru.aston.notificationservice.exception.enum_message;

import lombok.Getter;
@Getter
public enum ExceptionMessage {

    NOTIFICATION_PROFILE_NOT_FOUND("Notification profile is not found with id = ","404");

    private final String message;
    private final String code;

    ExceptionMessage(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
