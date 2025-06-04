package ru.aston.notificationservice.dao.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.notificationservice.AbstractTest;
import ru.aston.notificationservice.dao.entity.NotificationProfileEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"file:src/test/resources/db/clear-all.sql",
    "file:src/test/resources/db/inserts.sql"})
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NotificationProfileRepositoryTest extends AbstractTest {

  @Autowired
  private NotificationProfileRepository notificationProfileRepository;

  @Test
  void testFindByEmailNotification() {
    UUID expectedNotificationProfileId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    boolean hasEmailSubscription = true;

    NotificationProfileEntity notificationProfileEntity = notificationProfileRepository.findByEmailNotification(
        hasEmailSubscription).get();

    assertEquals(notificationProfileEntity.getId(), expectedNotificationProfileId);
  }

  @Test
  void testFindNotificationProfileById() {
    UUID notificationProfileId = UUID.fromString("12345678-1234-1234-1234-123456789abc");

    NotificationProfileEntity notificationProfileEntity = notificationProfileRepository.findNotificationProfileById(
        notificationProfileId).get();

    assertEquals(notificationProfileEntity.getId(), notificationProfileId);
  }

  @Test
  void testFindNotificationProfileByNullId() {
    assertThrows(NoSuchElementException.class,
        () -> notificationProfileRepository.findNotificationProfileById(
            null).get());
  }

  @Test
  void testSave() {
    NotificationProfileEntity notificationProfileEntity = NotificationProfileEntity.builder()
        .emailNotification(true)
        .smsNotification(true)
        .hasPushNotification(false)
        .build();

    NotificationProfileEntity newNotificationProfileEntity = notificationProfileRepository.save(
        notificationProfileEntity);

    assertNotNull(newNotificationProfileEntity.getId());
  }

  @Test
  void testSaveNullEntity() {
    assertThrows(InvalidDataAccessApiUsageException.class, () -> {
      notificationProfileRepository.save(null);
    });
  }
}
