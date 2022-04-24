package de.cofinpro.battleship.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattleshipTest {

    Battleship battleship;

    @BeforeEach
    void setUp() {
        battleship = new Battleship("rowAligned", 3);
        battleship.setRowAligned(true);
        battleship.setRow(0);
        battleship.setColumn(0);
    }

    @CsvSource({
            "1, 1",
            "2, 1"
    })
    @ParameterizedTest
    void whenColumnAlignedIsHit_hitsShipsReturnsTrue(int row, int column) {
        battleship = new Battleship("col", 2);
        battleship.setRowAligned(false);
        battleship.setRow(1);
        battleship.setColumn(1);
        assertTrue(battleship.hitsShip(row, column));
    }
    @CsvSource({
            "1, 0",
            "1, 2",
            "0, 0",
            "0, 1",
            "0, 2",
            "0, 3",
            "2, 0",
            "2, 2",
            "3, 0",
            "3, 1",
            "3, 2",
            "10, 10"
    })
    @ParameterizedTest
    void whenColumnAlignedIsMissed_hitsShipsReturnsFalse(int row, int column) {
        battleship = new Battleship("col", 2);
        battleship.setRowAligned(false);
        battleship.setRow(1);
        battleship.setColumn(1);
        assertFalse(battleship.hitsShip(row, column));
    }


    @CsvSource({
            "1, 4",
            "1, 5",
            "1, 0",
            "0, 0",
            "0, 1",
            "0, 2",
            "0, 3",
            "0, 4",
            "2, 0",
            "2, 1",
            "2, 2",
            "2, 3",
            "2, 4",
            "10, 10"
    })
    @ParameterizedTest
    void whenRowAlignedIsMissed_hitsShipsReturnsFalse(int row, int column) {
        battleship.setRow(1);
        battleship.setColumn(1);
        assertFalse(battleship.hitsShip(row, column));
    }

    @CsvSource({
            "0, 1",
            "0, 2",
            "0, 0"
    })
    @ParameterizedTest
    void whenRowAlignedIsHit_hitsShipsReturnsTrue(int row, int column) {
        assertTrue(battleship.hitsShip(row, column));
    }

    @Test
    void whenNewShip_remainingCellsEqualsShipCells() {
        assertEquals(battleship.getCells(), battleship.remainingCells());
    }

    @Test
    void whenShipGetsOneHit_remainingCellsIsOneLess() {
        assertTrue(battleship.hitsShip(0,2));
        assertEquals(battleship.getCells() - 1, battleship.remainingCells());
        assertTrue(battleship.hitsShip(0,0));
        assertEquals(battleship.getCells() - 2, battleship.remainingCells());
    }


    @Test
    void whenSamePositionHitAgain_remainingCellsStays() {
        assertTrue(battleship.hitsShip(0,2));
        assertTrue(battleship.hitsShip(0,2));
        assertEquals(battleship.getCells() - 1, battleship.remainingCells());
    }

    @Test
    void whenMiss_remainingCellsStays() {
        assertTrue(battleship.hitsShip(0,2));
        assertFalse(battleship.hitsShip(0,3));
        assertEquals(battleship.getCells() - 1, battleship.remainingCells());
    }


    @Test
    void whenAllHit_remainingCellsIsZero() {
        assertTrue(battleship.hitsShip(0,2));
        assertTrue(battleship.hitsShip(0,0));
        assertTrue(battleship.hitsShip(0,1));
        assertEquals(0, battleship.remainingCells());
    }
}