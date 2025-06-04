package ru.aston.notificationservice.dao.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.notificationservice.dao.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

}
