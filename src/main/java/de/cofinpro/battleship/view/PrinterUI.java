package de.cofinpro.battleship.view;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * simple UI class that wraps around the log4j Logger
 */
@Slf4j
public class PrinterUI {

    /**
     * printer method to display all propery settings to the user (on trace level).
     * @param properties the Properties hashmap of all application properties
     */
    public void printPropertiesInfo(Properties properties) {
        log.trace("Application parameters set as follows:");
        properties.keySet().stream()
                .sorted()
                .forEach(key -> log.trace(key + " = " + properties.getProperty((String) key)));
    }

    /**
     * printer method for printing standard (info level) messages to the user
     * @param message the message to print
     */
    public void info(String message) {
        log.info(message);
    }
}
