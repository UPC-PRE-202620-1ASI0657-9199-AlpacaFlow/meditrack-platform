package com.alpacaflow.meditrackplatform.iam.interfaces.rest;

import com.alpacaflow.meditrackplatform.iam.domain.services.UserCommandService;
import com.alpacaflow.meditrackplatform.iam.interfaces.rest.resources.CreateMockUserResource;
import com.alpacaflow.meditrackplatform.iam.interfaces.rest.resources.UserResource;
import com.alpacaflow.meditrackplatform.iam.interfaces.rest.transform.CreateMockUserCommandFromResourceAssembler;
import com.alpacaflow.meditrackplatform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Users endpoints consumed by other microservices (e.g. Organization Service) through their
 * anti-corruption layers. In the monolith these operations were in-memory calls to UserCommandService.
 * All endpoints require a valid JWT.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User lookup and creation endpoints used by other services")
public class UsersController {
    private final UserCommandService userCommandService;

    public UsersController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get a user by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        return userCommandService.getUserById(userId)
                .map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Get a user by its email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<UserResource> getUserByEmail(@PathVariable String email) {
        return userCommandService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a user with the given email and role (used when an admin registers a doctor or caregiver).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")})
    public ResponseEntity<UserResource> createUser(@RequestBody CreateMockUserResource resource) {
        try {
            var command = CreateMockUserCommandFromResourceAssembler.toCommandFromResource(resource);
            var user = userCommandService.handle(command);
            return new ResponseEntity<>(UserResourceFromEntityAssembler.toResourceFromEntity(user), HttpStatus.CREATED);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
