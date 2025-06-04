package ru.aston.notificationservice.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dao.repository.NotificationProfileRepository;
import ru.aston.notificationservice.dto.EmailNotificationProfileDto;
import ru.aston.notificationservice.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.notificationservice.dto.PushNotificationDto;
import ru.aston.notificationservice.dto.request.NotificationProfileBySmsRequestDto;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;
import ru.aston.notificationservice.exception.NotFoundNotificationProfileException;
import ru.aston.notificationservice.mapper.NotificationProfileMapper;
import ru.aston.notificationservice.service.NotificationProfileService;

@Service
@RequiredArgsConstructor
public class NotificationProfileServiceImpl implements NotificationProfileService {

  private final NotificationProfileRepository notificationProfileRepository;

  private final NotificationProfileMapper notificationProfileMapper;

  @Override
  public EmailNotificationProfileDto changeEmailNotificationProfile(UUID notificationProfileId,
      EmailNotificationProfileDto emailNotificationProfileDto) {
    NotificationProfileEntity notificationProfileEntity =
        notificationProfileRepository.findById(notificationProfileId)
            .orElseThrow(() -> new EntityNotFoundException(notificationProfileId,
                NotificationProfileEntity.class));

    notificationProfileEntity.setEmailNotification(
        emailNotificationProfileDto.hasEmailSubscription());

    notificationProfileRepository.save(notificationProfileEntity);
    return emailNotificationProfileDto;
  }

  @Override
  @Transactional
  public NotificationProfileBySmsResponseDto updateSmsNotification(
      NotificationProfileBySmsRequestDto notificationProfileBySmsRequestDto, UUID id) {
    NotificationProfileEntity notificationProfileEntity =
        notificationProfileRepository.findNotificationProfileById(id)
            .orElseThrow(() -> new NotFoundNotificationProfileException(
                String.format("NotificationProfile with id = %s was not found.", id)));
    notificationProfileEntity.setSmsNotification(
        notificationProfileBySmsRequestDto.smsNotification());
    notificationProfileRepository.save(notificationProfileEntity);
    return notificationProfileMapper.toNotificationProfileBySmsResponseDto(
        notificationProfileEntity);
  }

  @Override
  public PushNotificationDto updatePushNotification(UUID notificationProfileId,
      PushNotificationDto pushNotificationDto) {
    NotificationProfileEntity notificationProfileEntity = notificationProfileRepository
        .findById(notificationProfileId)
        .orElseThrow(() -> new EntityNotFoundException(notificationProfileId,
            NotificationProfileEntity.class));
    notificationProfileEntity.setHasPushNotification(pushNotificationDto.hasPushNotification());
    notificationProfileRepository.save(notificationProfileEntity);
    return pushNotificationDto;
  }

}