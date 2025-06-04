package ru.aston.notificationservice.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(UUID id, Class<?> clazz) {
    super(String.format("%s with id = %s not found", clazz.getSimpleName(), id));
  }

}
