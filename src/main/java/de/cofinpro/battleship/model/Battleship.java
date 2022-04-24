package de.cofinpro.battleship.model;

import lombok.Data;

import java.util.Arrays;

/**
 * class representing a battleship on the field
 * It is constructed with name and cells (e.g. length). Later the position and alignment can be stored
 * when the ship gets positioned on the battlefield
 */
@Data
public class Battleship {

    private final String name;
    private final int cells;
    // row and column contain the two lowest(!) array indices of the ship cells (0 to size - 1)
    private int row;
    private int column;
    private boolean isRowAligned;
    private final Boolean[] hitCells;

    public Battleship(String name, int cells) {
        this.name = name;
        this.cells = cells;
        this.hitCells = new Boolean[cells];
        Arrays.fill(hitCells, false);
    }

    /**
     * checks if this ship was hit by a shot. In that case the hit cell is set to true
     * @return true, if a ship's cell was hit, false else.
     */
    public boolean hitsShip(int hitRow, int hitColumn) {
        for (int i = 0; i < cells; i++) {
            int shipRow = isRowAligned ? row : row + i;
            int shipColumn = isRowAligned ? column + i : column;
            if (shipRow == hitRow && shipColumn == hitColumn) {
                hitCells[i] = true;
                return true;
            }
        }
        return false;
    }

    /**
     * @return the number of cells of this ship not hit yet.
     */
    public long remainingCells() {
        return Arrays.stream(hitCells).filter(cell -> !cell).count();
    }
}
