package ru.aston.notificationservice.exception;

public class NotFoundNotificationProfileException extends RuntimeException {
    public NotFoundNotificationProfileException(String message) {
        super(message);
    }
}
