package ru.aston.notificationservice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dao.repository.NotificationProfileRepository;
import ru.aston.notificationservice.dto.EmailNotificationProfileDto;
import ru.aston.notificationservice.exception.EntityNotFoundException;
import ru.aston.notificationservice.service.impl.NotificationProfileServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationProfileServiceImplTest {

  @Mock
  private NotificationProfileRepository notificationProfileRepository;

  @InjectMocks
  private NotificationProfileServiceImpl notificationProfileService;

  @Test
  void changeEmailNotificationProfile_whenNotificationProfileIdNotInDatabase_thenReturnEntityNotFoundException() {
    //given
    EmailNotificationProfileDto emailNotificationProfileDto = createEmailNotificationProfileDto();
    UUID notificationProfileId = UUID.randomUUID();

    //when
    when(notificationProfileRepository.findById(any())).thenThrow(
        EntityNotFoundException.class);

    //then
    assertThrows(EntityNotFoundException.class,
        () -> notificationProfileService.changeEmailNotificationProfile(notificationProfileId,
            emailNotificationProfileDto));

    verify(notificationProfileRepository, never()).save(any());

  }

  @Test
  void changeEmailNotificationProfile_whenNotificationProfileIdIsInTheDatabase_thenEmailNotificationProfileIsSuccessfullyChange(){
    //given
    NotificationProfileEntity notificationProfileEntity = createNotificationProfileEntity();
    EmailNotificationProfileDto emailNotificationProfileDto = createEmailNotificationProfileDto();
    Boolean expected = true;

    when(notificationProfileRepository.findById(any())).thenReturn(
        Optional.ofNullable(notificationProfileEntity));

    //when
    EmailNotificationProfileDto actual = notificationProfileService.changeEmailNotificationProfile(UUID.randomUUID(), emailNotificationProfileDto);

    //then
    Assertions.assertTrue(actual.hasEmailSubscription());

    verify(notificationProfileRepository, times(1)).save(
        Objects.requireNonNull(notificationProfileEntity));

  }


  private EmailNotificationProfileDto createEmailNotificationProfileDto() {
    return new EmailNotificationProfileDto(true);
  }

  private NotificationProfileEntity createNotificationProfileEntity(){
    return NotificationProfileEntity
        .builder()
        .id(UUID.randomUUID())
        .smsNotification(true)
        .emailNotification(false)
        .hasPushNotification(true)
        .build();
  }
}
