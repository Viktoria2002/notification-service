package ru.aston.notificationservice.service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.notificationservice.controller.SettingsNotificationsController;
import ru.aston.notificationservice.dto.EmailNotificationProfileDto;
import ru.aston.notificationservice.service.NotificationProfileService;

@WebMvcTest(SettingsNotificationsController.class)
class SettingsNotificationsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationProfileService notificationProfileService;

  @Test
  void  changeEmailNewsletters() throws Exception{
    UUID notificationProfileId = UUID.randomUUID();
    EmailNotificationProfileDto emailNotificationProfileDtoRequest = createEmailNotificationProfileDtoRequest();
    EmailNotificationProfileDto emailNotificationProfileDtoResponse = createEmailNotificationProfileDtoResponse();

    when(notificationProfileService.changeEmailNotificationProfile(notificationProfileId, emailNotificationProfileDtoRequest)).thenReturn(emailNotificationProfileDtoResponse);

    mockMvc.perform(patch("/settings/notifications/email/{notificationProfileId}", notificationProfileId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(emailNotificationProfileDtoRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasEmailSubscription").value(true));
  }

  private EmailNotificationProfileDto createEmailNotificationProfileDtoRequest(){
    return new EmailNotificationProfileDto(false);
  }
  private EmailNotificationProfileDto createEmailNotificationProfileDtoResponse(){
    return new EmailNotificationProfileDto(true);
  }
}
