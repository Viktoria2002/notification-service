package ru.aston.notificationservice.service;

public interface MessageService {

  void send(String to, String message, String subject);

}
