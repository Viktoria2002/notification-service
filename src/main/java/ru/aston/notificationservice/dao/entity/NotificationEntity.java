package ru.aston.notificationservice.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.aston.notificationservice.dao.enums.NotificationStatus;
import ru.aston.notificationservice.dao.enums.NotificationType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "notifications")
public class NotificationEntity extends AuditingBaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id", nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    @Column(name = "notification_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "notification_header", nullable = false, length = 30)
    private String notificationHeader;

    @Column(name = "notification_small_body", nullable = false, length = 70)
    private String notificationSmallBody;

    @Column(name = "notification_full_body")
    private String notificationFullBody;

    @Column(name = "foreign_link")
    private String foreignLink;

    @ManyToMany(mappedBy = "notifications")
    List<NotificationProfileEntity> notificationProfiles;

}