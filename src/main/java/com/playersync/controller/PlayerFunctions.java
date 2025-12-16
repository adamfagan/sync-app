package com.playersync.controller;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.playersync.dto.PlayerSyncDTO;
import com.playersync.service.PlayerSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.logging.Logger;

/*
 * TODO:
 * - field injection not recommended(unit test, tight coupling), use constructor injection instead with lombok annotation required args constructor
 * - magic strings, use constants
 * - page and size query parameters can throw numberFormatException, handle it please
 * - IllegalArgumentException is too generic, make custom exceptions for you cases
 * - PlayerSyncService null check is redundant -> proper constructor injection should be enough
 * - class should be named controller according to spring naming conventions
 * - Add unit tests for important parts of logic -> RedisCacheService, PlayerSyncService
 */
@Component
public class PlayerFunctions {

    @Autowired
    private PlayerSyncService playerSyncService;

    // GET /sync/players/{playerId}
    @FunctionName("syncPlayer")
    public HttpResponseMessage syncPlayer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "sync/players/{playerId}"
            ) HttpRequestMessage<Optional<String>> request,
            @BindingName("playerId") Long playerId,
            final ExecutionContext context) {

        // 1. Explicit Logging
        Logger logger = context.getLogger();
        logger.info(">>> STARTING SYNC FOR PLAYER ID: " + playerId);

        // 2. Fail-safe check for Service Injection
        if (playerSyncService == null) {
            logger.severe(">>> CRITICAL ERROR: Spring Context did not inject PlayerSyncService");
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal System Error: Dependency Injection Failed")
                    .build();
        }

        if (playerId == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid Player ID").build();
        }

        try {
            // Check for pagination params
            String pageStr = request.getQueryParameters().getOrDefault("page", "0");
            String sizeStr = request.getQueryParameters().getOrDefault("size", "0");

            logger.info("Fetching data from Service...");
            PlayerSyncDTO result = playerSyncService.syncPlayer(playerId, Integer.parseInt(pageStr), Integer.parseInt(sizeStr));
            logger.info("Data fetched successfully.");

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(result)
                    .build();

        } catch (IllegalArgumentException e) {
            logger.warning("Player not found: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Player not found").build();
        } catch (Exception e) {
            logger.severe(">>> UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected failure: " + e.getMessage()).build();
        }
    }

    // BONUS 2: PUT Function
    @FunctionName("updatePlayer")
    public HttpResponseMessage updatePlayer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "update/players/{playerId}"
            ) HttpRequestMessage<String> request,
            @BindingName("playerId") Long playerId,
            final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info(">>> STARTING UPDATING AND SYNC FOR PLAYER ID: " + playerId);

        if (playerSyncService == null) {
            logger.severe(">>> CRITICAL ERROR: Spring Context did not inject PlayerSyncService");
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal System Error: Dependency Injection Failed")
                    .build();
        }

        try {
            String newStatus = request.getBody();
            logger.info("Updating player " + playerId + " to status " + newStatus);

            playerSyncService.updatePlayer(playerId, newStatus);

            return request.createResponseBuilder(HttpStatus.OK).body("Updated and Cache Invalidated").build();
        } catch (Exception e) {
            logger.severe("Update failed: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
    }
}