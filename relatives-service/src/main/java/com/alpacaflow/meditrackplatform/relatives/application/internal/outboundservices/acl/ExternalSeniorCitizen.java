package com.alpacaflow.meditrackplatform.relatives.application.internal.outboundservices.acl;

import java.util.Date;

/**
 * Local copy of the senior citizen data owned by the Organization Service.
 * Only the fields the Relatives context needs are mapped.
 */
public record ExternalSeniorCitizen(
        Long id,
        Long organizationId,
        String firstName,
        String lastName,
        Date birthDate,
        String gender,
        Double weight,
        String dni,
        Double height,
        String imageUrl,
        Long deviceId
) {
}
