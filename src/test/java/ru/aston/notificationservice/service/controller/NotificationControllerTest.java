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
import ru.aston.notificationservice.controller.NotificationController;
import ru.aston.notificationservice.dto.request.NotificationProfileBySmsRequestDto;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.service.NotificationProfileService;
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationProfileService notificationProfileService;

  @Test
  void updateSmsNotification_successfulUpdateSmsNotification() throws Exception {
    UUID notificationProfileId = UUID.randomUUID();
    NotificationProfileBySmsRequestDto notificationProfileBySmsRequestDto = createNotificationProfileBySmsRequestDto();
    NotificationProfileBySmsResponseDto notificationProfileBySmsResponseDto = createNotificationProfileBySmsResponseDto();

    when(this.notificationProfileService.updateSmsNotification(notificationProfileBySmsRequestDto,
        notificationProfileId)).thenReturn(notificationProfileBySmsResponseDto);

    mockMvc.perform(patch("/settings/notifications/sms/{notificationProfileId}", notificationProfileId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(notificationProfileBySmsRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.smsNotification").value(true));
  }

  @Test
  void updateSmsNotification_whenNotificationProfileIdNotInDataBase_thenReturnNotFoundNotificationProfileException() throws Exception{
    UUID notificationProfileId = UUID.randomUUID();
    NotificationProfileBySmsRequestDto notificationProfileBySmsRequestDto = createNotificationProfileBySmsRequestDto();

    when(this.notificationProfileService.updateSmsNotification(notificationProfileBySmsRequestDto,
        notificationProfileId)).thenThrow(NotFoundNotificationProfileException.class);

    mockMvc.perform(patch("/settings/notifications/sms/{notificationProfileId}", notificationProfileId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notificationProfileBySmsRequestDto)))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundNotificationProfileException));
  }

  private NotificationProfileBySmsRequestDto createNotificationProfileBySmsRequestDto() {
    return NotificationProfileBySmsRequestDto
        .builder()
        .smsNotification(false)
        .build();
  }

  private NotificationProfileBySmsResponseDto createNotificationProfileBySmsResponseDto() {
    return NotificationProfileBySmsResponseDto
        .builder()
        .smsNotification(true)
        .build();
  }
}
