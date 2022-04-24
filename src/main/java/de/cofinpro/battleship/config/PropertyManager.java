package de.cofinpro.battleship.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * PropertyManager class handling all application properties using java.util.Properties.
 * On class initializing, first defaults are set and then the application.properties file is loaded from the constant
 * path specified. The class provides static methods for retrieving a property by key, resp. all properties.
 */
@Slf4j
public class PropertyManager {
    private static final Properties APP_CONFIG = new Properties();
    private static final String PROPERTIES_PATH = "./src/main/resources/application.properties";

    private PropertyManager() {
        // no instantiation
    }

    /**
     * get all property settings as the Properties instance (hash map).
     * @return the properties.
     */
    public static Properties getProperties() {
        return APP_CONFIG;
    }

    /**
     * get a property value by a given key
     * @param key a property given
     * @return the value to the key (null if not existing)
     */
    public static String getProperty(String key) {
        return APP_CONFIG.getProperty(key);
    }

    static {
        setPropertyDefaults();
        loadProperties();
    }

    /**
     * read property file from path given - overwriting the defaults for those set
     */
    private static void loadProperties() {
        try (Reader reader = new FileReader(PROPERTIES_PATH)) {
            APP_CONFIG.load(reader);
        } catch (FileNotFoundException exception) {
            log.info("No file application.properties found - using defaults.");
        } catch (IOException exception) {
            log.error("IO-Exception reading application.properties: " + PROPERTIES_PATH);
        }
    }

    /**
     * setting all default property values
     */
    private static void setPropertyDefaults() {
        APP_CONFIG.setProperty("field-size", "10");
        APP_CONFIG.setProperty("water-symbol", "~ ");
        APP_CONFIG.setProperty("own-ship-symbol", "O ");
        APP_CONFIG.setProperty("hit-symbol", "X ");
        APP_CONFIG.setProperty("miss-symbol", "M ");
        APP_CONFIG.setProperty("five-cell-ships", "1");
        APP_CONFIG.setProperty("four-cell-ships", "1");
        APP_CONFIG.setProperty("three-cell-ships", "2");
        APP_CONFIG.setProperty("two-cell-ships", "1");
        // comma separated names list, which is consumed from largest to smallest ship (ignoring if more, error if less)
        APP_CONFIG.setProperty("ship-names", "Aircraft Carrier,Battleship,Submarine,Cruiser,Destroyer");
        APP_CONFIG.setProperty("error-msg-wrong-coords", "Error! Wrong coordinates given! Try again:");
        APP_CONFIG.setProperty("error-msg-ship-length", "Error! Wrong length of the %s! Try again:");
        APP_CONFIG.setProperty("error-msg-ship-location", "Error! Wrong ship location! Try again:");
        APP_CONFIG.setProperty("error-msg-ship-too-close", "Error! You placed it too close to another one. Try again:");
        APP_CONFIG.setProperty("msg-place-ships", "%s, place your ships on the game field");
        APP_CONFIG.setProperty("msg-change-player", "Press Enter and pass the move to another player");
        APP_CONFIG.setProperty("msg-hit", "\nYou hit a ship!");
        APP_CONFIG.setProperty("msg-miss", "\nYou missed!");
        APP_CONFIG.setProperty("msg-shot", "\n%s, it's your turn:");
        APP_CONFIG.setProperty("msg-win", "\nYou sank the last ship. You won. Congratulations!");
        APP_CONFIG.setProperty("msg-sink", "\nYou sank a ship!");
        APP_CONFIG.setProperty("test-property", "standard");
    }
}
