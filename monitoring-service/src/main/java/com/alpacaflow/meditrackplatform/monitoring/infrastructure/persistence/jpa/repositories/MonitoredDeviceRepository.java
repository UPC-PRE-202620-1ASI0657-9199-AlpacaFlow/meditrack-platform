package com.alpacaflow.meditrackplatform.monitoring.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.MonitoredDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonitoredDeviceRepository extends JpaRepository<MonitoredDevice, Long> {
    Optional<MonitoredDevice> findByDeviceId(Long deviceId);
}
