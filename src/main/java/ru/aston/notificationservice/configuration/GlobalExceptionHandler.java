package ru.aston.notificationservice.configuration;

import java.net.ConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.aston.notificationservice.exception.EntityNotFoundException;
import ru.aston.notificationservice.exception.ErrorMessage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.aston.notificationservice.exception.EntityNotFoundException;
import ru.aston.notificationservice.exception.ErrorMessage;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.exception.enum_message.ExceptionMessage;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ConnectException.class)
  public ResponseEntity<String> handleInternalServerError() {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Internal Server Error");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleNotFoundPassportException(EntityNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessage("404", ex.getMessage()));
  }


  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorMessage handleNotFoundNotificationProfileException(
      NotFoundNotificationProfileException ex) {
    log.warn(ex.getMessage());
    return new ErrorMessage(
        ExceptionMessage.NOTIFICATION_PROFILE_NOT_FOUND.getCode(),
        ex.getMessage()
    );
  }
}
/*
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessage("404", ex.getMessage()));
  }

}*/
