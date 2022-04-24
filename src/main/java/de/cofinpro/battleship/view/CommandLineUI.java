package de.cofinpro.battleship.view;

import de.cofinpro.battleship.config.PropertyManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * UI class that wraps around the log4j Logger for printing messages and takes over all
 * the prompting of a user for coordinate inputs or player changes. (to be mocked for integration tests).
 */
@Slf4j
public class CommandLineUI {

    private Scanner scanner = new Scanner(System.in);

    private static final String ENTER_SHIP_FORMAT = "%nEnter the coordinates of the %s (%d cells):";

    /**
     * prompts for user input position strings (e.g. B3 B6) for a given ship with given (cell) length.
     * The method loops until the user gave two non-empty string tokens.
     * @param shipName the ship name as displayed
     * @param length the cell length as displayed
     * @return the whitespace split tokens.
     */
    public List<String> promptForShipPosition(String shipName, int length) {
        List<String> tokens;
        do {
            log.info(String.format(ENTER_SHIP_FORMAT, shipName, length));
            tokens = Arrays.stream(scanner.nextLine().split("\\s+")).toList();
        } while (tokens.size() != 2);
        return tokens;
    }

    /**
     * prompt for a shot position and loops until non-empty input given.
     * @return the scanner provided token
     */
    public String promptForShotPosition() {
        String token;
        do {
            token = scanner.nextLine();
        } while (token.isEmpty());
        return token;
    }

    /**
     * printer method for printing standard (info level) messages to the user
     * @param message the message to print
     */
    public void info(String message) {
        log.info(message);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void trace(String message) {
        log.trace(message);
    }

    public void error(String message) {
        log.error(message);
    }

    /**
     * display a "press enter" message and wait for it.
     */
    public void promptForPlayerChange() {
        info(PropertyManager.getProperty("msg-change-player"));
        scanner.nextLine();
    }
}
