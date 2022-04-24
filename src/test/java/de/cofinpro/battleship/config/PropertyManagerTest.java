package de.cofinpro.battleship.config;

import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertyManagerTest {

    @Test
    void getPropertyReturnsDefaults() {
        assertEquals("~ ", PropertyManager.getProperty("water-symbol"));
        assertEquals("M ", PropertyManager.getProperty("miss-symbol"));
    }

    @Test
    void getPropertyOverloadsDefaultsFromPropertiesFile() {
        assertEquals("overridden", PropertyManager.getProperty("test-property"));
    }

    @Test
    void getPropertiesReturnsAllDefaults() {
        Properties properties = PropertyManager.getProperties();
        assertEquals("~ ", properties.getProperty("water-symbol"));
        assertEquals("Error! Wrong ship location! Try again:",
                properties.getProperty("error-msg-ship-location"));
    }

    @Test
    void getPropertiesLetsOverridePropertiesProgramatically() {
        Properties properties = PropertyManager.getProperties();
        properties.setProperty("two-cell-ships", "20");
        assertEquals("20", PropertyManager.getProperty("two-cell-ships"));
        properties.setProperty("two-cell-ships", "1");
    }
}