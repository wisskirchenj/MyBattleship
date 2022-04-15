package de.cofinpro.battleship.controller;

/**
 * custom exception thrown when unrecoverable inconsistency in properties is detected,
 * to controlled crash the application with the information
 */
public class ApplicationPropertiesException extends RuntimeException {
    public ApplicationPropertiesException(String message) {
        super(message);
    }
}
