package com.alpacaflow.meditrackplatform.monitoring.domain.exceptions;

/** Thrown when the Devices Service cannot be reached to validate a device. */
public class DevicesServiceUnavailableException extends RuntimeException {
    public DevicesServiceUnavailableException(String detail) {
        super("Devices Service is not available: %s".formatted(detail));
    }
}
