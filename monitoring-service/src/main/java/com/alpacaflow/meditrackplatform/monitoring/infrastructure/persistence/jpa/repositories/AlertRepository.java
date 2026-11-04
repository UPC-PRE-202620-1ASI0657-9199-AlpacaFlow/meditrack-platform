package com.alpacaflow.meditrackplatform.monitoring.infrastructure.persistence.jpa.repositories;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Alert aggregate
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
}

