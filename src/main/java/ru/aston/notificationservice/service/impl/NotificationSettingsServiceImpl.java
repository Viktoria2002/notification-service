package ru.aston.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dao.repository.NotificationProfileRepository;
import ru.aston.notificationservice.dto.response.NotificationProfileDto;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.exception.enum_message.ExceptionMessage;
import ru.aston.notificationservice.service.NotificationSettingsService;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final NotificationProfileRepository notificationProfileRepository;

    @Override
    public NotificationProfileDto getNotificationSettings(String notificationProfileId) {
        NotificationProfileEntity entity = findNotificationProfileEntityById(notificationProfileId);
        return NotificationProfileDto.builder()
                .emailNotification(entity.getEmailNotification())
                .pushNotification(entity.getHasPushNotification())
                .smsNotification(entity.getSmsNotification())
                .build();
    }

    @Override
    public NotificationProfileEntity findNotificationProfileEntityById(String notificationProfileId) {
        return notificationProfileRepository.findById(UUID.fromString(notificationProfileId))
                .orElseThrow(() ->
                        new NotFoundNotificationProfileException(ExceptionMessage.NOTIFICATION_PROFILE_NOT_FOUND.getMessage() + notificationProfileId)
                );
    }
}
