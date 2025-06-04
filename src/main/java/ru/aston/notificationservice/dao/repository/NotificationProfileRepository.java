package ru.aston.notificationservice.dao.repository;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;

public interface NotificationProfileRepository extends
    JpaRepository<NotificationProfileEntity, UUID> {

  Optional<NotificationProfileEntity> findByEmailNotification(boolean hasEmailSubscription);

  Optional<NotificationProfileEntity> findNotificationProfileById(UUID id);
}
