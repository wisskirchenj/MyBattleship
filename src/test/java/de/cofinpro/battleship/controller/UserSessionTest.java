package de.cofinpro.battleship.controller;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battleship;
import de.cofinpro.battleship.model.Shot;
import de.cofinpro.battleship.model.ShotResult;
import de.cofinpro.battleship.view.CommandLineUI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserSessionTest {

    @InjectMocks
    private UserSession userSession = new UserSession("Player 1");

    @Mock
    private CommandLineUI commandLineUI;

    @Test
    void initBattleField() {
        PropertyManager.getProperties().setProperty("field-size", "1");
        assertThrows(ApplicationPropertiesException.class, () -> userSession.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "27");
        assertThrows(ApplicationPropertiesException.class, () -> userSession.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "2");
        assertNotNull(userSession.initBattleField());
        PropertyManager.getProperties().setProperty("field-size", "26");
        assertNotNull(userSession.initBattleField());
    }

    @Test
    void whenPropertiesMatch_initFleetReturnsAllShips() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        PropertyManager.getProperties().setProperty("four-cell-ships", "0");
        PropertyManager.getProperties().setProperty("three-cell-ships", "1");
        PropertyManager.getProperties().setProperty("two-cell-ships", "0");
        PropertyManager.getProperties().setProperty("ship-names", "5cell ship,3cell ship");
        assertEquals(2, userSession.initFleet().size());
        assertEquals("5cell ship", userSession.initFleet().get(0).getName());
        assertEquals(5, userSession.initFleet().get(0).getCells());
        assertEquals("3cell ship", userSession.initFleet().get(1).getName());
        assertEquals(3, userSession.initFleet().get(1).getCells());
    }

    @Test
    void whenPropertiesDontMatch_initFleetIgnoresOrThrows() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        PropertyManager.getProperties().setProperty("four-cell-ships", "1");
        PropertyManager.getProperties().setProperty("three-cell-ships", "1");
        PropertyManager.getProperties().setProperty("two-cell-ships", "0");
        PropertyManager.getProperties().setProperty("ship-names", "5cell ship,3cell ship");
        assertThrows(ApplicationPropertiesException.class, () -> userSession.initFleet());
        PropertyManager.getProperties().setProperty("four-cell-ships", "0");
        PropertyManager.getProperties().setProperty("three-cell-ships", "0");
        assertEquals(1, userSession.initFleet().size());
        assertEquals("5cell ship", userSession.initFleet().get(0).getName());
    }

    @Test
    void whenOne5CellShip_getNCellsShipsReturnsOneShip() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "1");
        assertEquals(1, userSession.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2"), 0).size());
        assertEquals("ship1", userSession.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2"), 0).get(0).getName());
    }

    @Test
    void whenMoreShipsThanNamesLeft_getNCellsShipsThrows() {
        PropertyManager.getProperties().setProperty("five-cell-ships", "2");
        List<String> shipNames = List.of("ship1", "ship2");
        assertThrows(ApplicationPropertiesException.class, () -> userSession.getNCellsShips(5, "five-cell-ships",
                shipNames, 1));
        assertDoesNotThrow(() -> userSession.getNCellsShips(5, "five-cell-ships",
                List.of("ship1", "ship2", "ship3"), 1));
    }

    @Test
    void getValidUserPosition() {
        PropertyManager.getProperties().setProperty("field-size", "10");
        userSession.initBattleField();
        when(commandLineUI.promptForShipPosition(anyString(), anyInt())).thenReturn(List.of("E6", "E9"));
        Battleship battleship = new Battleship("test", 4);
        userSession.userPositionShip(battleship);
        verify(commandLineUI, times(1)).promptForShipPosition(anyString(), anyInt());
        when(commandLineUI.promptForShipPosition(anyString(), anyInt())).thenReturn(List.of("E6", "D4"), List.of("D1", "D4"));
        userSession.userPositionShip(battleship);
        verify(commandLineUI, times(1 + 2)).promptForShipPosition(anyString(), anyInt());
    }

    @ParameterizedTest
    @ValueSource(strings =  {"F5", "H5", "G5"})
    void whenShipPositionGiven_shotGivesHit(String position) {
        when(commandLineUI.promptForShipPosition("test", 3)).thenReturn(List.of("H5", "F5"));
        userSession.userPositionShip(new Battleship("test", 3));

        when(commandLineUI.promptForShotPosition()).thenReturn(position);
        Shot shot = userSession.shoots();
        assertEquals(4, shot.getColumn());

        ShotResult result = userSession.applyShot(shot);
        assertEquals(ShotResult.HIT, result);
    }

    @Test
    void whenAllShipPositionsHit_applyShotGivesSunk() {
        PropertyManager.getProperties().setProperty("field-size", "7");
        PropertyManager.getProperties().setProperty("five-cell-ships", "0");
        PropertyManager.getProperties().setProperty("four-cell-ships", "0");
        PropertyManager.getProperties().setProperty("three-cell-ships", "0");
        PropertyManager.getProperties().setProperty("two-cell-ships", "2");
        userSession.initFleet();
        when(commandLineUI.promptForShipPosition(anyString(), anyInt())).thenReturn(List.of("H5", "G5"), List.of("A1", "A2"));
        doNothing().when(commandLineUI).promptForPlayerChange();
        when(commandLineUI.promptForShotPosition()).thenReturn("A1", "A2");
        userSession.userAddOwnShipsToBattleField();
        userSession.getBattlefieldUI().displayBattlefield();

        ShotResult result = userSession.applyShot(userSession.shoots());
        assertEquals(ShotResult.HIT, result);
        result = userSession.applyShot(userSession.shoots());
        assertEquals(ShotResult.SUNK, result);
    }

    @ParameterizedTest
    @ValueSource(strings =  {"E5", "I5", "F4", "G4", "H4", "H6", "F6", "G6", "A1", "J10"})
    void whenNonShipPositionGiven_shotGivesMiss(String position) {
        System.out.println(userSession);
        when(commandLineUI.promptForShipPosition("test", 3)).thenReturn(List.of("H5", "F5"));
        userSession.userPositionShip(new Battleship("test", 3));

        when(commandLineUI.promptForShotPosition()).thenReturn(position);
        Shot shot = userSession.shoots();

        ShotResult result = userSession.applyShot(shot);
        assertEquals(ShotResult.MISSED, result);
    }
}