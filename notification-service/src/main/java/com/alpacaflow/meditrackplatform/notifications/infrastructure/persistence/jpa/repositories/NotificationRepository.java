package com.alpacaflow.meditrackplatform.notifications.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByAlertId(Long alertId);

    List<Notification> findAllByOrderBySentAtDesc();

    List<Notification> findAllByDeviceIdOrderBySentAtDesc(Long deviceId);
}
