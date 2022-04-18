package de.cofinpro.battleship.model;

import de.cofinpro.battleship.view.BattlefieldCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldTest {

    Battlefield battlefield;

    @BeforeEach
    void setUp() {
        battlefield = new Battlefield(10);
    }

    static Stream<Arguments> provideInvalidPosition() {
        return Stream.of(
                Arguments.of("FF"),
                Arguments.of("H"),
                Arguments.of("A0"),
                Arguments.of("A11"),
                Arguments.of("55")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPosition")
    void whenInvalidPositionGiven_isValidShotGivesEmpty(String position) {
        assertTrue(battlefield.couldPositionShip(List.of("F5", "H5"), new Battleship("test", 3)));
        Optional<Shot> shot = battlefield.isValidShot(position);
        assertTrue(shot.isEmpty());
    }

    static Stream<Arguments> provideShipPosition() {
        return Stream.of(
                Arguments.of("F5"),
                Arguments.of("H5"),
                Arguments.of("G5")
        );
    }

    @ParameterizedTest
    @MethodSource("provideShipPosition")
    void whenShipPositionGiven_isValidShotGivesHit(String position) {
        assertTrue(battlefield.couldPositionShip(List.of("F5", "H5"), new Battleship("test", 3)));
        Optional<Shot> shot = battlefield.isValidShot(position);
        assertTrue(shot.isPresent());
        assertFalse(shot.get().isMissed());
        assertEquals(4, shot.get().getPosition().column);
        Battlefield.Indices indices = shot.get().getPosition();
        assertEquals(BattlefieldCell.HIT, battlefield.getField()[indices.row][indices.column]);
    }

    static Stream<Arguments> provideWaterPosition() {
        return Stream.of(
                Arguments.of("E5"),
                Arguments.of("I5"),
                Arguments.of("F4"),
                Arguments.of("G4"),
                Arguments.of("H4"),
                Arguments.of("H6"),
                Arguments.of("F6"),
                Arguments.of("G6"),
                Arguments.of("A1"),
                Arguments.of("J10")
        );
    }

    @ParameterizedTest
    @MethodSource("provideWaterPosition")
    void whenNonShipPositionGiven_isValidShotGivesMiss(String position) {
        assertTrue(battlefield.couldPositionShip(List.of("F5", "H5"), new Battleship("test", 3)));
        Optional<Shot> shot = battlefield.isValidShot(position);
        assertTrue(shot.isPresent());
        assertTrue(shot.get().isMissed());
        Battlefield.Indices indices = shot.get().getPosition();
        assertEquals(BattlefieldCell.MISS, battlefield.getField()[indices.row][indices.column]);
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
        int row = ship.getRow();
        int column = ship.getColumn();
        for (int i = 0; i < ship.getCells(); i++) {
            assertEquals(BattlefieldCell.SHIP, battlefield.getField()[row][column + i]);
        }
        assertEquals(BattlefieldCell.WATER, battlefield.getField()[row][column + ship.getCells()]);
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
        assertTrue(Arrays.deepEquals(battlefield.getField(), new Battlefield(10).getField()));
    }

    @Test
    void whenShipsFit_ShipFitsWithOtherShipsWorks() {
        assertTrue(battlefield.couldPositionShip(List.of("A3", "A6"), new Battleship("a", 4)));
        Battleship battleship = new Battleship("b", 3);
        battleship.setRowAligned(true);
        battleship.setRow(3);
        battleship.setColumn(1);
        assertTrue(battlefield.shipFitsWithOtherShips(battleship));
        assertTrue(battlefield.couldPositionShip(List.of("D2", "D4"), new Battleship("b", 3)));
        battleship = new Battleship("c", 2);
        battleship.setRowAligned(false);
        battleship.setRow(0);
        battleship.setColumn(0);
        assertTrue(battlefield.shipFitsWithOtherShips(battleship));
        assertTrue(battlefield.couldPositionShip(List.of("B1", "A1"), new Battleship("c", 2)));
        battleship = new Battleship("d", 5);
        battleship.setRowAligned(false);
        battleship.setRow(0);
        battleship.setColumn(7);
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
                Arguments.of(new int[] {5, 0}, new int[] {5, 3}, new Battleship("n", 4)),
                Arguments.of(new int[] {0, 1}, new int[] {0, 0},  new Battleship("b", 2)),
                Arguments.of(new int[] {3, 5}, new int[] {7, 5},  new Battleship("b", 5)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 1},  new Battleship("b", 4))
        );
    }

    @ParameterizedTest
    @MethodSource("provideMatchingShipLength")
    void whenShipLengthMatch_matchesShipLengthWorks(int[] indices1, int[] indices2, Battleship ship) {
        List<Battlefield.Indices> indicesList = new ArrayList<>();
        indicesList.add(battlefield.new Indices(indices1[0], indices1[1]));
        indicesList.add(battlefield.new Indices(indices2[0], indices2[1]));
        ship.setRowAligned(indices1[0] == indices2[0]);
        assertTrue(battlefield.matchesShipLength(indicesList, ship));
    }

    static Stream<Arguments> provideNonMatchingShipLength() {
        return Stream.of(
                Arguments.of(new int[] {5, 0}, new int[] {5, 3}, new Battleship("n", 2)),
                Arguments.of(new int[] {0, 1}, new int[] {0, 0},  new Battleship("b", 3)),
                Arguments.of(new int[] {3, 5}, new int[] {7, 5},  new Battleship("b", 2)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 1},  new Battleship("b", 5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonMatchingShipLength")
    void whenShipLengthDontMatch_matchesShipLengthFails(int[] indices1, int[] indices2, Battleship ship) {
        List<Battlefield.Indices> indicesList = new ArrayList<>();
        indicesList.add(battlefield.new Indices(indices1[0], indices1[1]));
        indicesList.add(battlefield.new Indices(indices2[0], indices2[1]));
        ship.setRowAligned(indices1[0] == indices2[0]);
        assertFalse(battlefield.matchesShipLength(indicesList, ship));
    }

    static Stream<Arguments> provideAlignedPositions() {
        return Stream.of(
                Arguments.of(new int[] {5, 0}, new int[] {5, 3}, new Battleship("n", 10)),
                Arguments.of(new int[] {5, 1}, new int[] {0, 1},  new Battleship("b", 1)),
                Arguments.of(new int[] {3, 5}, new int[] {7, 5},  new Battleship("b", 5)),
                Arguments.of(new int[] {4, 1}, new int[] {4, 1},  new Battleship("b", 4))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAlignedPositions")
    void whenPositionsAligned_positionsAlignedOnFieldWorksAndShipInitialized
            (int[] indices1, int[] indices2, Battleship ship) {
        List<Battlefield.Indices> indicesList = new ArrayList<>();
        indicesList.add(battlefield.new Indices(indices1[0], indices1[1]));
        indicesList.add(battlefield.new Indices(indices2[0], indices2[1]));
        assertTrue(battlefield.positionsAreAlignedOnField(indicesList, ship));
        assertEquals(ship.isRowAligned(), indices1[0] == indices2[0]);
        assertEquals(ship.getRow(), Math.min(indices1[0],indices2[0]));
        assertEquals(ship.getColumn(), Math.min(indices1[1],indices2[1]));
    }

    static Stream<Arguments> provideNonAlignedPositions() {
        return Stream.of(
                Arguments.of(new int[] {5, 0}, new int[] {4, 1}, new Battleship("n", 2)),
                Arguments.of(new int[] {0, 1}, new int[] {7, 0},  new Battleship("b", 3)),
                Arguments.of(new int[] {4, 1}, new int[] {1, 3},  new Battleship("b", 5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonAlignedPositions")
    void whenPositionsNotAligned_positionsAlignedOnFieldFailsAndShipNotAltered(
            int[] indices1, int[] indices2,  Battleship ship) {
        List<Battlefield.Indices> indicesList = new ArrayList<>();
        indicesList.add(battlefield.new Indices(indices1[0], indices1[1]));
        indicesList.add(battlefield.new Indices(indices2[0], indices2[1]));
        assertFalse(battlefield.positionsAreAlignedOnField(indicesList, ship));
        assertEquals(0, ship.getRow());
        assertEquals(0, ship.getColumn());
        assertFalse(ship.isRowAligned());
    }

    static Stream<Arguments> provideInvalidPositionTokens() {
        return Stream.of(
                Arguments.of("1"),
                Arguments.of("A"),
                Arguments.of("AA"),
                Arguments.of("1234"),
                Arguments.of("K1234"),
                Arguments.of("A11"),
                Arguments.of("A0"),
                Arguments.of("K10"),
                Arguments.of("---"),
                Arguments.of("A1 A2"),
                Arguments.of("A%$/")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPositionTokens")
    void whenInvalidToken_parsePositionTokenEmpty(String token) {
        assertTrue(battlefield.parsePositionToken(token).isEmpty());
    }

    static Stream<Arguments> provideValidPositionTokens() {
        return Stream.of(
                Arguments.of("A1"),
                Arguments.of("a1"),
                Arguments.of("A10"),
                Arguments.of("J10"),
                Arguments.of("I7")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidPositionTokens")
    void whenValidToken_parsePositionTokenReturnsIndices(String token) {
        assertTrue(battlefield.parsePositionToken(token).isPresent());
    }

    @Test
    void whenTokenA1_parsePositionTokenWorks() {
        assertTrue(battlefield.parsePositionToken("A1").isPresent());
        assertEquals(0, battlefield.parsePositionToken("A1").get().row);
        assertEquals(0, battlefield.parsePositionToken("A1").get().column);
    }

    @Test
    void whenTokenJ10_parsePositionTokenWorks() {
        assertTrue(battlefield.parsePositionToken("J10").isPresent());
        assertEquals(9, battlefield.parsePositionToken("J10").get().row);
        assertEquals(9, battlefield.parsePositionToken("J10").get().column);
    }
}