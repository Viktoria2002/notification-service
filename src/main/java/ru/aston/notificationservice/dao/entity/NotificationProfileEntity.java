package ru.aston.notificationservice.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@Table(name = "notification_profile")
public class NotificationProfileEntity extends AuditingBaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_profile_id", nullable = false)
    private UUID id;

    @Column(name = "sms_notification", nullable = false, columnDefinition = "boolean default false")
    private Boolean smsNotification;

    @Column(name = "push_notification", nullable = false, columnDefinition = "boolean default true")
    private Boolean hasPushNotification;

    @Column(name = "email_notification", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailNotification;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "user_notifications",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_profile_id"))
    private List<NotificationEntity> notifications;

}