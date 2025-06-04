package ru.aston.notificationservice.service.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.notificationservice.controller.SettingsController;
import ru.aston.notificationservice.dto.PushNotificationDto;
import ru.aston.notificationservice.exception.EntityNotFoundException;
import ru.aston.notificationservice.service.NotificationProfileService;

@WebMvcTest(SettingsController.class)
class SettingsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationProfileService notificationProfileService;

  @Test
  void updatePushNotification_successfulUpdatePushNotification() throws Exception {
    UUID notificationProfileId = UUID.randomUUID();
    PushNotificationDto requestPushNotificationDto = createPushNotificationDtoRequest();
    PushNotificationDto responsePushNotificationDto = createPushNotificationDtoResponse();

    when(notificationProfileService.updatePushNotification(notificationProfileId,
        requestPushNotificationDto)).thenReturn(responsePushNotificationDto);

    mockMvc.perform(
            patch("/settings/notifications/push/{notificationProfileId}", notificationProfileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPushNotificationDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasPushNotification").value(false));
  }

  @Test
  void updatePushNotification_whenNotificationProfileIdNotInDataBase_thenReturnEntityNotFoundException()
      throws Exception {
    UUID notificationProfileId = UUID.randomUUID();
    PushNotificationDto requestPushNotificationDto = createPushNotificationDtoRequest();
    PushNotificationDto responsePushNotificationDto = createPushNotificationDtoResponse();

    when(notificationProfileService.updatePushNotification(notificationProfileId,
        requestPushNotificationDto)).thenThrow(EntityNotFoundException.class);

    mockMvc.perform(
            patch("/settings/notifications/push/{notificationProfileId}", notificationProfileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPushNotificationDto)))
        .andExpect(status().isNotFound())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
  }

  private PushNotificationDto createPushNotificationDtoRequest() {
    return new PushNotificationDto(true);
  }

  private PushNotificationDto createPushNotificationDtoResponse() {
    return new PushNotificationDto(false);
  }
}
