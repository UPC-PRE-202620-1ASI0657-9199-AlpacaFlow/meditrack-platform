package com.alpacaflow.meditrackplatform.organization.application.internal.outboundservices.acl;

/**
 * Local view of an IAM user, as returned by the IAM Service.
 */
public record ExternalUser(
        Long userId,
        String email,
        String role
) {
}
