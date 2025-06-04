package ru.aston.notificationservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.aston.notificationservice.util.Fixtures.getNotificationProfileBySmsRequestDto;
import static ru.aston.notificationservice.util.Fixtures.getNotificationProfileEntity;
import static ru.aston.notificationservice.util.Fixtures.getNotificationProfileEntityWithWrongId;
import static ru.aston.notificationservice.util.Fixtures.notificationProfileBySmsResponseDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.notificationservice.TestNotificationServiceApplication;
import ru.aston.notificationservice.dao.repository.NotificationProfileRepository;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.mapper.NotificationProfileMapper;
import ru.aston.notificationservice.service.NotificationProfileService;

@SpringBootTest
@Import(TestNotificationServiceApplication.class)
@Testcontainers(disabledWithoutDocker = true)
public class NotificationProfileServiceImplTest {

  @Autowired
  private NotificationProfileRepository notificationProfileRepository;

  @Autowired
  private NotificationProfileService notificationProfileService;

  @Mock
  private NotificationProfileMapper notificationProfileMapper;

  @AfterEach
  public void destroy() {
    notificationProfileRepository.deleteAll();
  }

  @Test
  public void shouldUpdateSmsNotificationOnTrue() {
    //given
    var notificationProfileEntity = getNotificationProfileEntity();
    notificationProfileRepository.save(notificationProfileEntity);
    var notificationProfileBySmsRequestDto = getNotificationProfileBySmsRequestDto();

    //when
    when(notificationProfileMapper.toNotificationProfileBySmsResponseDto(any()))
        .thenReturn(notificationProfileBySmsResponseDto());
    notificationProfileService.updateSmsNotification(notificationProfileBySmsRequestDto,
        notificationProfileEntity.getId());

    //then
    var notificationEntity = notificationProfileRepository.findById(notificationProfileEntity.getId());
    assertNotNull(notificationEntity);
    assertEquals(Boolean.TRUE, notificationEntity.get().getSmsNotification());
  }

  @Test
  public void shouldNotUpdateSmsNotificationWithWrongId() {
    //given
    var notificationProfileEntity = getNotificationProfileEntity();
    notificationProfileRepository.save(notificationProfileEntity);
    var notificationProfileBySmsRequestDto = getNotificationProfileBySmsRequestDto();
    var notificationProfileEntityWrong = getNotificationProfileEntityWithWrongId();

    //when

    //then
    assertThrows(
        NotFoundNotificationProfileException.class,
        () -> notificationProfileService.updateSmsNotification(notificationProfileBySmsRequestDto,
            notificationProfileEntityWrong.getId()));

  }

}
