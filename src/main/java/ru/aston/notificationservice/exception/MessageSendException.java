package ru.aston.notificationservice.exception;

public class MessageSendException extends RuntimeException {

  public MessageSendException(String message) {
    super(message);
  }
}
