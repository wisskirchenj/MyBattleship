package de.cofinpro.battleship.view;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battlefield;
import de.cofinpro.battleship.model.Battleship;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldUITest {

    @Test
    void whenEmpty_displayBattlefieldOnlyHasWater() {
        BattlefieldUI battlefieldUI = new BattlefieldUI(new Battlefield(10));
        String stringRep = battlefieldUI.displayBattlefield();
        String[] lines = stringRep.split("\n");
        // 11 lines plus 1 newline extra at start
        assertEquals(11 + 1, lines.length);
        assertEquals(11 * 2, lines[1].length());
        assertEquals(11 * 2, lines[7].length());
        assertTrue(lines[2].endsWith(PropertyManager.getProperty("water-symbol").repeat(10)));
        assertTrue(lines[2].startsWith("A "));
    }

    @Test
    void whenShipPositioned_displayBattlefieldShowsShip() {
        Battlefield battlefield = new Battlefield(10);
        battlefield.couldPositionShip(List.of("E1","E4"), new Battleship("test", 4));
        BattlefieldUI battlefieldUI = new BattlefieldUI(battlefield);
        String stringRep = battlefieldUI.displayBattlefield();
        String[] lines = stringRep.split("\n");
        assertTrue(lines[6].endsWith(PropertyManager.getProperty("water-symbol").repeat(6)));
        assertTrue(lines[6].startsWith("E " + PropertyManager.getProperty("own-ship-symbol").repeat(4)));
    }

    @Test
    void whenShipPositioned_displayBattlefieldObscuredShowsNoShip() {
        Battlefield battlefield = new Battlefield(10);
        battlefield.couldPositionShip(List.of("E1","E4"), new Battleship("test", 4));
        BattlefieldUI battlefieldUI = new BattlefieldUI(battlefield);
        String stringRep = battlefieldUI.displayBattlefieldObscured();
        String[] lines = stringRep.split("\n");
        assertTrue(lines[6].endsWith(PropertyManager.getProperty("water-symbol").repeat(10)));
        assertTrue(lines[6].startsWith("E "));
    }
}