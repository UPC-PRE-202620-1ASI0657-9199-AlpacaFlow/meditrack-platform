package com.alpacaflow.meditrackplatform.relatives.application.internal.commandservices;

import com.alpacaflow.meditrackplatform.relatives.application.internal.outboundservices.acl.ExternalOrganizationService;
import com.alpacaflow.meditrackplatform.relatives.domain.model.aggregates.Relative;
import com.alpacaflow.meditrackplatform.relatives.domain.model.commands.CreateRelativeCommand;
import com.alpacaflow.meditrackplatform.relatives.domain.model.valueobjects.PlanType;
import com.alpacaflow.meditrackplatform.relatives.domain.services.RelativeCommandService;
import com.alpacaflow.meditrackplatform.relatives.infrastructure.persistence.repositories.RelativeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

/**
 * Implementation of the RelativeCommandService interface.
 */
@Service
public class RelativeCommandServiceImpl implements RelativeCommandService {
    private final RelativeRepository relativeRepository;
    private final ExternalOrganizationService externalOrganizationService;

    public RelativeCommandServiceImpl(RelativeRepository relativeRepository, ExternalOrganizationService externalOrganizationService) {
        this.relativeRepository = relativeRepository;
        this.externalOrganizationService = externalOrganizationService;
    }

    @Override
    @Transactional
    public Relative handle(CreateRelativeCommand command) {
        // Get the senior citizen from the Organization Service (ACL over HTTP)
        var organizationSeniorCitizen = externalOrganizationService.fetchSeniorCitizenById(command.seniorCitizenId())
                .orElseThrow(() -> new IllegalArgumentException("Senior citizen with id %d not found".formatted(command.seniorCitizenId())));

        // Validate that the senior citizen belongs to a relative organization
        var organizationType = externalOrganizationService.fetchOrganizationTypeById(organizationSeniorCitizen.organizationId())
                .orElse(null);
        if (!"relative".equals(organizationType)) {
            throw new IllegalArgumentException("Senior citizen with id %d does not belong to a relative organization".formatted(command.seniorCitizenId()));
        }

        // Convert the external senior citizen to the Relatives bounded context SeniorCitizen entity
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String birthDateStr = dateFormat.format(organizationSeniorCitizen.birthDate());

        var relativesSeniorCitizen = new com.alpacaflow.meditrackplatform.relatives.domain.model.entities.SeniorCitizen(
                organizationSeniorCitizen.firstName(),
                organizationSeniorCitizen.lastName(),
                organizationSeniorCitizen.dni(),
                organizationSeniorCitizen.gender(),
                organizationSeniorCitizen.height().floatValue(),
                birthDateStr,
                organizationSeniorCitizen.weight().floatValue(),
                organizationSeniorCitizen.imageUrl(),
                String.valueOf(organizationSeniorCitizen.deviceId())
        );

        // Parse planType
        PlanType planType;
        try {
            planType = PlanType.valueOf(command.planType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid plan type: %s. Must be FREEMIUM or PREMIUM".formatted(command.planType()));
        }

        // Create the Relative
        Relative relative;
        if (command.userId() != null && command.userId() > 0) {
            relative = new Relative(
                    command.firstName(),
                    command.lastName(),
                    command.phoneNumber(),
                    command.userId(),
                    relativesSeniorCitizen
            );
        } else {
            relative = new Relative(
                    command.firstName(),
                    command.lastName(),
                    command.phoneNumber(),
                    relativesSeniorCitizen
            );
        }

        // Set plan type
        relative.setPlan(planType);

        try {
            var savedRelative = relativeRepository.save(relative);
            // Note: Relative doesn't have publishCreatedEvent() method yet
            // If domain events are needed, add the method to Relative aggregate
            return savedRelative;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving relative: %s".formatted(e.getMessage()));
        }
    }
}

