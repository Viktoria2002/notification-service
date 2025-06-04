package ru.aston.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;
import ru.aston.notificationservice.dto.response.NotificationProfileBySmsResponseDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationProfileMapper {

  @Mapping(target = "smsNotification", source = "smsNotification")
  NotificationProfileBySmsResponseDto toNotificationProfileBySmsResponseDto(
      NotificationProfileEntity notificationProfileEntity);
}

