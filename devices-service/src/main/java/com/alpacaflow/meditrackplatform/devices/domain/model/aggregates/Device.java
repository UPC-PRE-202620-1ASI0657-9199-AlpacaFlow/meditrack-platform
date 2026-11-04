package com.alpacaflow.meditrackplatform.devices.domain.model.aggregates;

import com.alpacaflow.meditrackplatform.devices.domain.model.commands.CreateDeviceCommand;
import com.alpacaflow.meditrackplatform.devices.domain.model.valueobjects.EDeviceStatus;
import com.alpacaflow.meditrackplatform.devices.domain.model.valueobjects.Holder;
import com.alpacaflow.meditrackplatform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

/*** Device aggregate root
 * @summary
 * This aggregate root represents a medical device.
 * A device has a model, status and holder. Its vital sign measurements belong to the Monitoring Service.
 * @since 1.0
 */
@Getter
@Entity
public class Device extends AuditableAbstractAggregateRoot<Device> {
    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EDeviceStatus status;

    @Embedded
    private Holder holder;

    public Device() {
        this.model = Strings.EMPTY;
        this.status = EDeviceStatus.ACTIVE;
        this.holder = new Holder();
    }

    /**
     * Create a new device with the given model and holder
     * @param model The model of the device
     * @param holderId The holder ID
     */
    public Device(String model, Long holderId) {
        this();
        this.model = model;
        this.status = EDeviceStatus.ACTIVE;
        this.holder = new Holder(holderId, "SeniorCitizen");
    }

    /**
     * Create a new device with information from the command
     * @param command The command to create the device
     * @see CreateDeviceCommand
     */
    public Device(CreateDeviceCommand command) {
        this(command.model(), command.holderId());
    }
}
