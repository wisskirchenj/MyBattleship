package de.cofinpro.battleship.view;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * simple UI wrapper class around java.util.Scanner, that is able to prompt and get user input.
 */
@Slf4j
public class ScannerUI {

    private Scanner scanner;

    private static final String ENTER_SHIP_FORMAT = "%nEnter the coordinates of the %s (%d cells):";
    private static final String ENTER_SHOT = "\nTake a shot!";

    public ScannerUI() {
        this.scanner = new Scanner(System.in);
    }

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
            log.info(ENTER_SHOT);
            token = scanner.nextLine();
        } while (token.isEmpty());
        return token;
    }
}
