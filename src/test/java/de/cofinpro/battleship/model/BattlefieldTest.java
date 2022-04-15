package de.cofinpro.battleship.model;

import de.cofinpro.battleship.config.PropertyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldTest {

    Battlefield battlefield;

    @BeforeEach
    void setUp() {
        battlefield = new Battlefield(10);
    }

    static Stream<Arguments> provideRowAlignedShipAndPosition() {
        return Stream.of(
                Arguments.of(List.of("E1", "E4" ), new Battleship("n", 4)),
                Arguments.of(List.of("E7", "E8" ), new Battleship("b", 2))
        );
    }

    @ParameterizedTest
    @MethodSource("provideRowAlignedShipAndPosition")
    void whenValidPosition_couldPositionShipPositionsShip(List<String> positions, Battleship ship) {
        assertTrue(battlefield.couldPositionShip(positions, ship));
        assertTrue(battlefield.toString().contains(
                PropertyManager.getProperty("own-ship-symbol").repeat(ship.getCells())));
        assertFalse(battlefield.toString().contains(
                PropertyManager.getProperty("own-ship-symbol").repeat(ship.getCells() + 1)));
    }

    static Stream<Arguments> provideInvalidShipPosition() {
        return Stream.of(
                Arguments.of(List.of("E1", "E4"), new Battleship("n", 3)),
                Arguments.of(List.of("E33", "E8"), new Battleship("b", 2)),
                Arguments.of(List.of("11", "E8"), new Battleship("b", 2)),
                Arguments.of(List.of("AAAA", "E8"), new Battleship("b", 2)),
                Arguments.of(List.of("D7", "E8"), new Battleship("b", 2)),
                Arguments.of(List.of("D7", "D2"), new Battleship("b", 2))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidShipPosition")
    void whenInvalidPosition_couldPositionShipPositionsNoShip(List<String> positions, Battleship ship) {
        assertFalse(battlefield.couldPositionShip(positions, ship));
        assertFalse(battlefield.toString().contains(PropertyManager.getProperty("own-ship-symbol")));
    }

    @Test
    void whenShipsFit_ShipFitsWithOtherShipsWorks() {
        assertTrue(battlefield.couldPositionShip(List.of("A3", "A6"), new Battleship("a", 4)));
        Battleship battleship = new Battleship("b", 3);
        battleship.setRowAligned(true);
        battleship.setXPos(3);
        battleship.setYPos(1);
        assertTrue(battlefield.shipFitsWithOtherShips(battleship));
        assertTrue(battlefield.couldPositionShip(List.of("D2", "D4"), new Battleship("b", 3)));
        battleship = new Battleship("c", 2);
        battleship.setRowAligned(false);
        battleship.setXPos(0);
        battleship.setYPos(0);
        assertTrue(battlefield.shipFitsWithOtherShips(battleship));
        assertTrue(battlefield.couldPositionShip(List.of("B1", "A1"), new Battleship("c", 2)));
        battleship = new Battleship("d", 5);
        battleship.setRowAligned(false);
        battleship.setXPos(0);
        battleship.setYPos(7);
        assertTrue(battlefield.shipFitsWithOtherShips(battleship));
        assertTrue(battlefield.couldPositionShip(List.of("A8", "E8"), new Battleship("d", 5)));
    }

    static Stream<Arguments> provideTooCloseShips() {
        return Stream.of(
                Arguments.of(List.of("E1", "E4"), new Battleship("1", 4),
                        List.of("D1", "F1"), new Battleship("2", 3)),
                Arguments.of(List.of("E2", "E4"), new Battleship("1", 3),
                        List.of("D1", "F1"), new Battleship("2", 3)),
                Arguments.of(List.of("E2", "E4"), new Battleship("1", 3),
                        List.of("G1", "F1"), new Battleship("2", 2)),
                Arguments.of(List.of("E2", "E4"), new Battleship("1", 3),
                        List.of("H3", "F3"), new Battleship("2", 3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTooCloseShips")
    void whenShipsDontFit_ShipFitsWithOtherShipsFails(List<String> first, Battleship ship1,
                                                      List<String> second, Battleship ship2) {
        assertTrue(battlefield.couldPositionShip(first, ship1));
        assertFalse(battlefield.couldPositionShip(second, ship2));
    }

    static Stream<Arguments> provideMatchingShipLength() {
        return Stream.of(
                Arguments.of(new int[] {5, 5}, new int[] {0, 3}, new Battleship("n", 4)),
                Arguments.of(new int[] {0, 0}, new int[] {1, 0},  new Battleship("b", 2)),
                Arguments.of(new int[] {3, 7}, new int[] {5, 5},  new Battleship("b", 5)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 1},  new Battleship("b", 4))
        );
    }

    @ParameterizedTest
    @MethodSource("provideMatchingShipLength")
    void whenShipLengthMatch_matchesShipLengthWorks(int[] xpos, int[] ypos, Battleship ship) {
        assertTrue(battlefield.matchesShipLength(xpos, ypos, ship));
    }

    static Stream<Arguments> provideNonMatchingShipLength() {
        return Stream.of(
                Arguments.of(new int[] {5, 5}, new int[] {0, 3}, new Battleship("n", 2)),
                Arguments.of(new int[] {0, 0}, new int[] {1, 0},  new Battleship("b", 3)),
                Arguments.of(new int[] {3, 7}, new int[] {5, 5},  new Battleship("b", 2)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 1},  new Battleship("b", 5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonMatchingShipLength")
    void whenShipLengthDontMatch_matchesShipLengthFails(int[] xpos, int[] ypos, Battleship ship) {
        assertFalse(battlefield.matchesShipLength(xpos, ypos, ship));
    }

    static Stream<Arguments> provideAlignedPositions() {
        return Stream.of(
                Arguments.of(new int[] {5, 5}, new int[] {0, 3}, new Battleship("n", 10)),
                Arguments.of(new int[] {5, 0}, new int[] {1, 1},  new Battleship("b", 1)),
                Arguments.of(new int[] {3, 7}, new int[] {5, 5},  new Battleship("b", 5)),
                Arguments.of(new int[] {4, 4}, new int[] {1, 1},  new Battleship("b", 4))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAlignedPositions")
    void whenPositionsAligned_positionsAlignedOnFieldWorks(int[] xpos, int[] ypos, Battleship ship) {
        assertTrue(battlefield.positionsAreAlignedOnField(xpos, ypos, ship));
    }

    static Stream<Arguments> provideNonAlignedPositions() {
        return Stream.of(
                Arguments.of(new int[] {-1, 4}, new int[] {0, 0}, new Battleship("n", 0)),
                Arguments.of(new int[] {5, 5}, new int[] {0, -1}, new Battleship("n", 11)),
                Arguments.of(new int[] {4, 4}, new int[] {-1, -1}, new Battleship("n", 5)),
                Arguments.of(new int[] {5, 4}, new int[] {0, 1}, new Battleship("n", 2)),
                Arguments.of(new int[] {0, 7}, new int[] {1, 0},  new Battleship("b", 3)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 3},  new Battleship("b", 5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonAlignedPositions")
    void whenPositionsNotAligned_positionsAlignedOnFieldFails(int[] xpos, int[] ypos, Battleship ship) {
        assertFalse(battlefield.positionsAreAlignedOnField(xpos, ypos, ship));
    }

    @Test
    void testToString() {
        String stringRep = battlefield.toString();
        String[] lines = stringRep.split("\n");
        // 11 lines plus 1 newline extra at start
        assertEquals(11 + 1, lines.length);
        assertEquals(11 * 2, lines[1].length());
        assertEquals(11 * 2, lines[7].length());
        assertTrue(lines[2].endsWith(PropertyManager.getProperty("water-symbol").repeat(10)));
        assertTrue(lines[2].startsWith("A"));
    }
}