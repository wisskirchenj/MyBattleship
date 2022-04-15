package de.cofinpro.battleship.controller;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battlefield;
import de.cofinpro.battleship.model.Battleship;
import de.cofinpro.battleship.view.ScannerUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattleshipControllerTest {

    @Mock
    ScannerUI scanner;

    @InjectMocks
    BattleshipController battleshipController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void initBattleField() {
        PropertyManager.getProperties().setProperty("field-size", "1");
        assertThrows(ApplicationPropertiesException.class, () -> battleshipController.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "27");
        assertThrows(ApplicationPropertiesException.class, () -> battleshipController.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "2");
        assertDoesNotThrow(() -> battleshipController.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "26");
        assertDoesNotThrow(() -> battleshipController.initBattleField());
    }

    @Test
    void whenPropertiesMatch_initFleetReturnsAllShips() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        PropertyManager.getProperties().setProperty("four-cell-ships", "0");
        PropertyManager.getProperties().setProperty("three-cell-ships", "1");
        PropertyManager.getProperties().setProperty("two-cell-ships", "0");
        PropertyManager.getProperties().setProperty("ship-names", "5cell ship,3cell ship");
        assertEquals(2, battleshipController.initFleet().size());
        assertEquals("5cell ship", battleshipController.initFleet().get(0).getName());
        assertEquals(5, battleshipController.initFleet().get(0).getCells());
        assertEquals("3cell ship", battleshipController.initFleet().get(1).getName());
        assertEquals(3, battleshipController.initFleet().get(1).getCells());
    }

    @Test
    void whenPropertiesDontMatch_initFleetIgnoresOrThrows() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        PropertyManager.getProperties().setProperty("four-cell-ships", "1");
        PropertyManager.getProperties().setProperty("three-cell-ships", "1");
        PropertyManager.getProperties().setProperty("two-cell-ships", "0");
        PropertyManager.getProperties().setProperty("ship-names", "5cell ship,3cell ship");
        assertThrows(ApplicationPropertiesException.class, () -> battleshipController.initFleet());
        PropertyManager.getProperties().setProperty("four-cell-ships", "0");
        PropertyManager.getProperties().setProperty("three-cell-ships", "0");
        assertEquals(1, battleshipController.initFleet().size());
        assertEquals("5cell ship", battleshipController.initFleet().get(0).getName());
    }

    @Test
    void whenOne5CellShip_getNCellsShipsReturnsOneShip() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        assertEquals(1, battleshipController.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2"), 0).size());
        assertEquals("ship1", battleshipController.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2"), 0).get(0).getName());
    }

    @Test
    void whenMoreShipsThanNamesLeft_getNCellsShipsThrows() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "2");
        List<String> shipNames = List.of("ship1", "ship2");
        assertThrows(ApplicationPropertiesException.class, () -> battleshipController.getNCellsShips(5, "five-cell-ships",
                shipNames, 1));
        assertDoesNotThrow(() -> battleshipController.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2", "ship3"), 1));
    }

    @Test
    void getValidUserPosition() {
        PropertyManager.getProperties().setProperty("field-size", "10");
        battleshipController.initBattleField();
        when(scanner.promptForShipPosition(anyString(), anyInt())).thenReturn(List.of("E6", "E9"));
        Battleship battleship = new Battleship("test", 4);
        battleshipController.getValidUserPosition(battleship);
        verify(scanner, times(1)).promptForShipPosition(anyString(), anyInt());
        when(scanner.promptForShipPosition(anyString(), anyInt())).thenReturn(List.of("E6", "D4"), List.of("D1", "D4"));
        battleshipController.getValidUserPosition(battleship);
        verify(scanner, times(1 + 2)).promptForShipPosition(anyString(), anyInt());
    }
}