package de.cofinpro.battleship.model;

import de.cofinpro.battleship.config.PropertyManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

/**
 * class representing the battlefield, that stores the ships and hits, misses in a field of cell with
 * configurable size.
 */
@Slf4j
public class Battlefield {

    /**
     * enumeration type which is the building block of a battlefield cell.
     * It gives its printable Symbol-String by the method getFieldSymbol(), which is a configurable property.
     */
    enum Cell {
        WATER(PropertyManager.getProperty("water-symbol")),
        SHIP(PropertyManager.getProperty("own-ship-symbol")),
        HIT(PropertyManager.getProperty("hit-symbol")),
        MISS(PropertyManager.getProperty("miss-symbol"));
        private final String cellSymbol;

        Cell(String symbol) {
            this.cellSymbol = symbol;
        }

        public String getCellSymbol() {
            return cellSymbol;
        }
    }

    private final Cell[][] field;
    private final String[] rowTitles;
    private final String[] columnTitles;
    private final int size;

    public Battlefield(int size) {
        this.size = size;
        field = new Cell[size][size];
        rowTitles = new String[size];
        columnTitles = new String[size];
        IntStream.range(0, size * size).forEach(n -> field[n / size][n % size] = Cell.WATER);
        IntStream.range(0, size).forEach(n -> rowTitles[n] = String.format("%-2d", n + 1));
        IntStream.range(0, size).forEach(n -> columnTitles[n] = String.format("%-2c", 'A' + n));
    }

    /**
     * method gets a list with 2 position strings (e.g. ["B2", "D3"]) and checks, if the tokens are indeed valid
     * positions on the battlefield and if the given ship can be positioned there. If it is possible, the ship is
     * positioned on the field, if not the battlefield remains unchanged.
     * @param positions by caller guaranteed list of two non-empty elements.
     * @param ship the battleship to position
     * @return true, if position algorithm is able to position ship with given input
     */
    public boolean couldPositionShip(List<String> positions, Battleship ship) {
        int[] xIndices = getXIndicesFromPositions(positions);
        int[] yIndices = getYIndicesFromPositions(positions);

        if (positionsAreAlignedOnField(xIndices, yIndices, ship)
                && matchesShipLength(xIndices, yIndices, ship)
                && shipFitsWithOtherShips(ship)) {
            positionShip(ship);
            return true;
        }
        return false;
    }

    /**
     * stores the ship into the field after all position checks have been successfully passed.
     * @param ship ship to position
     */
    private void positionShip(Battleship ship) {
        for (int i = 0; i < ship.getCells(); i++) {
            if (ship.isRowAligned()) {
                field[ship.getXPos()][ship.getYPos() + i] = Cell.SHIP;
            } else {
                field[ship.getXPos() + i][ship.getYPos()] = Cell.SHIP;
            }
        }
    }

    /**
     * checks, if the ship can be positioned without touching other fleet ships. This is the case,
     * if a rectangle around the ship with border width 1 cell is free of other ships - i.e. contains only water.
     * @param ship ship to check positioning
     * @return true if check passes
     */
     boolean shipFitsWithOtherShips(Battleship ship) {
        int x = ship.getXPos();
        int y = ship.getYPos();
        int l = ship.getCells();
        if (ship.isRowAligned()
                && isWaterInRectangle(Math.max(0, x - 1), Math.min(size - 1, x + 1),
                                     Math.max(0, y - 1), Math.min(size - 1, y + l))
            || !ship.isRowAligned()
                && isWaterInRectangle(Math.max(0, x - 1), Math.min(size - 1, x + l),
                                     Math.max(0, y - 1), Math.min(size - 1, y + 1))) {
            return true;
        }
        log.error(PropertyManager.getProperty("error-msg-ship-too-close"));
        return false;
    }

    /**
     * checks if a given rectangle inside the field has only water in it (for positioning a ship)
     * @param xFrom lower x index
     * @param xTo upper x index
     * @param yFrom lower y index
     * @param yTo upper y index
     * @return the check result
     */
    private boolean isWaterInRectangle(int xFrom, int xTo, int yFrom, int yTo) {
        for (int x = xFrom; x <= xTo; x++) {
            for (int y = yFrom; y <= yTo; y++) {
                if (field[x][y] != Cell.WATER) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks, if the ship fits exactly into the user given cell area
     * @param x the two x coordinates from the user input
     * @param y the two y coordinates from the user input
     * @param ship the ship to position
     * @return the check result, if the ship fits
     */
     boolean matchesShipLength(int[] x, int[] y, Battleship ship) {
        if (Math.abs(y[0] - y[1]) == ship.getCells() - 1 || Math.abs(x[0] - x[1]) == ship.getCells() - 1) {
            ship.setXPos(Math.min(x[0], x[1]));
            ship.setYPos(Math.min(y[0], y[1]));
            return true;
        }
        log.error(String.format(PropertyManager.getProperty("error-msg-ship-length"), ship.getName()));
        return false;
    }

    /**
     * checks, if the user given cell positions are valid (none contains -1, which is set for invalid coordinates
     * by previous conversions getX(orY)IndicesFromPositions ). and whether they are in a row or a column (=aligned).
     * @param x the two x coordinates from the user input
     * @param y the two y coordinates from the user input
     * @param ship the ship to position
     * @return the check result, if the ship fits
     */
     boolean positionsAreAlignedOnField(int[] x, int[] y, Battleship ship) {
        if (x[0] < 0 || x[1] < 0 || y[0] < 0 || y[1] < 0) {
            log.error(PropertyManager.getProperty("error-msg-wrong-coords"));
            return false;
        }
        if (x[0] == x[1]) {
            ship.setRowAligned(true);
            return true;
        }
        if (y[0] == y[1]) {
            ship.setRowAligned(false);
            return true;
        }
        log.error(PropertyManager.getProperty("error-msg-ship-location"));
        return false;
    }

    /**
     * converts the two tokens from user position input into x indices of the battlefield. If conversion fails, -1
     * is set to this x index.
     * @param positions list of guaranteed two non-empty user input token strings.
     * @return the two x coordinates as int[]
     */
    private int[] getYIndicesFromPositions(List<String> positions) {
        int[] yIndices = new int[positions.size()];
        for (int i = 0; i < yIndices.length; i ++) {
            if (positions.get(i).length() < 2 || !positions.get(i).substring(1).matches("\\d+")) {
                yIndices[i] = -1;
            } else {
                yIndices[i] = indexInRangeOrDefault(Integer.parseInt(positions.get(i).substring(1)) - 1);
            }
        }
        return yIndices;
    }

    /**
     * converts the two tokens from user position input into x indices of the battlefield. If conversion fails, -1
     * is set to this x index.
     * @param positions list of guaranteed two non-empty user input token strings.
     * @return the two x coordinates as int[]
     */
    private int[] getXIndicesFromPositions(List<String> positions) {
        int[] xIndices = new int[positions.size()];
        for (int i = 0; i < xIndices.length; i ++) {
            xIndices[i] = indexInRangeOrDefault(positions.get(i).toCharArray()[0] - 'A');
        }
        return xIndices;
    }

    /**
     * helper method to check, if an int is a valid index (in [0,size - 1]
     * @param tryIndex int to try to use as index
     * @return the given index or -1 if outside the interval.
     */
    private int indexInRangeOrDefault(int tryIndex) {
        if (tryIndex < 0 || tryIndex >= size) {
            return -1;
        }
        return tryIndex;
    }

    /**
     * string representation of the battlefield as used for the printerUI when logging.
     * @return string representation of the battlefield
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n  ");
        builder.append(String.join("", rowTitles));
        for (int i = 0; i < size; i++) {
            builder.append("\n").append(columnTitles[i]);
            for (int j = 0; j < size; j++) {
                builder.append(field[i][j].getCellSymbol());
            }
        }
        return builder.toString();
    }
}
