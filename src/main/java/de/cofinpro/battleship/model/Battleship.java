package de.cofinpro.battleship.model;

import lombok.Data;

/**
 * simple class representing a battleship on the field
 * It is constructed with name and cells (e.g. length). Later the position and alignment can be stored
 * when the ship gets positioned on the battlefield
 */
@Data
public class Battleship {

    public Battleship(String name, int cells) {
        this.name = name;
        this.cells = cells;
    }

    private final String name;
    private final int cells;
    //positions xPos, yPos contain the lower array index of both ship boundary cells (0 to size - 1)
    private int xPos;
    private int yPos;
    private boolean isRowAligned;
}
