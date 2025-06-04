package ru.aston.notificationservice.service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.notificationservice.controller.impl.NotificationSettingsControllerImpl;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;
import ru.aston.notificationservice.service.NotificationSettingsService;

@WebMvcTest(NotificationSettingsControllerImpl.class)
class NotificationSettingsControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private NotificationSettingsService notificationSettingsService;

  @Test
  void getClientNotificationsSettings_clientNotificationSettingsSuccessfullyReceived()
      throws Exception {
    String notificationProfileId = String.valueOf(UUID.randomUUID());
    NotificationProfileDto notificationProfileDto = createNotificationProfileDto();

    when(
        this.notificationSettingsService.getNotificationSettings(notificationProfileId)).thenReturn(
        notificationProfileDto);

    mvc.perform(get("/settings/notifications/all/{notificationProfileId}", notificationProfileId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.smsNotification").value("false"))
        .andExpect(jsonPath("$.pushNotification").value("false"))
        .andExpect(jsonPath("$.emailNotification").value("false"));
  }

  private NotificationProfileDto createNotificationProfileDto() {
    return NotificationProfileDto
        .builder()
        .emailNotification(false)
        .smsNotification(false)
        .pushNotification(false)
        .build();
  }
}
