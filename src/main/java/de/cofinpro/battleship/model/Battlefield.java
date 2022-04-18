package de.cofinpro.battleship.model;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.view.BattlefieldCell;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * class representing the battlefield, that stores the ships and hits, misses in a field of cells with
 * configurable size.
 */
@Slf4j
public class Battlefield {

    class Indices {
        int row;
        int column;

        public Indices(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

    private final BattlefieldCell[][] field;
    private final int size;

    public Battlefield(int size) {
        this.size = size;
        field = new BattlefieldCell[size][size];
        IntStream.range(0, size * size).forEach(n -> field[n / size][n % size] = BattlefieldCell.WATER);
    }

    public int getSize() {
        return size;
    }

    public BattlefieldCell[][] getField() {
        return field;
    }

    /**
     * method gets a list with 2 position strings (e.g. ["B2", "D3"]) and checks, if the tokens are indeed valid
     * positions on the battlefield and if the given ship can be positioned there. If it is possible, the ship is
     * positioned on the field, if not the battlefield remains unchanged.
     * @param positionTokens by caller guaranteed list of two non-empty string elements.
     * @param ship the battleship to position
     * @return true, if position algorithm is able to position ship with given input
     */
    public boolean couldPositionShip(List<String> positionTokens, Battleship ship) {
        List<Indices> indices= new ArrayList<>();
        positionTokens.forEach(token -> parsePositionToken(token).ifPresent(indices::add));
        if (indices.size() < 2) {
            log.error(PropertyManager.getProperty("error-msg-wrong-coords"));
            return false;
        }

        if (positionsAreAlignedOnField(indices, ship)
                && matchesShipLength(indices, ship)
                && shipFitsWithOtherShips(ship)) {
            positionShip(ship);
            return true;
        }
        return false;
    }

    /**
     * Gets a position string (e.g. "B2"). If the token is indeed valid, i.e. it can be parsed and does not
     * hit an own ship, a random hit success is generated and stored in a new Shot object AND in the
     * battlefield.
     * @param positionToken by caller guaranteed non-empty string.
     * @return empty, if no valid position, a shot object with success information and position of the shot
     */
    public Optional<Shot> isValidShot(String positionToken) {
        Optional<Shot> optionalShot = parsePositionToken(positionToken).map(
                indices -> new Shot(indices, field[indices.row][indices.column] != BattlefieldCell.SHIP)
        );
        optionalShot.ifPresentOrElse(shot ->
                field[shot.getPosition().row][shot.getPosition().column]
                        = shot.isMissed() ? BattlefieldCell.MISS : BattlefieldCell.HIT,
                () -> log.error(PropertyManager.getProperty("error-msg-wrong-coords")));
        return optionalShot;
    }

    /**
     * Parses a string position token given by user into an Indices object with row and column indices.
     * @param token string token to parse
     * @return optional with parse result as Indices object or empty if parse fails
     */
    Optional<Indices> parsePositionToken(String token) {
        int row = token.toUpperCase().toCharArray()[0] - 'A';
        if (row < 0 || row >= size || token.length() < 2
                || !token.substring(1).matches("\\d+")) {
            return Optional.empty();
        }
        int column = Integer.parseInt(token.substring(1)) - 1;
        if (column < 0 || column >= size) {
            return Optional.empty();
        }
        return Optional.of(new Indices(row, column));
    }

    /**
     * stores the ship into the field after all position checks have been successfully passed.
     * @param ship ship to position
     */
    private void positionShip(Battleship ship) {
        for (int i = 0; i < ship.getCells(); i++) {
            if (ship.isRowAligned()) {
                field[ship.getRow()][ship.getColumn() + i] = BattlefieldCell.SHIP;
            } else {
                field[ship.getRow() + i][ship.getColumn()] = BattlefieldCell.SHIP;
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
        int row = ship.getRow();
        int column = ship.getColumn();
        int cells = ship.getCells();
        if (ship.isRowAligned()
                && isWaterInRectangle(Math.max(0, row - 1), Math.min(size - 1, row + 1),
                                     Math.max(0, column - 1), Math.min(size - 1, column + cells))
            || !ship.isRowAligned()
                && isWaterInRectangle(Math.max(0, row - 1), Math.min(size - 1, row + cells),
                                     Math.max(0, column - 1), Math.min(size - 1, column + 1))) {
            return true;
        }
        log.error(PropertyManager.getProperty("error-msg-ship-too-close"));
        return false;
    }

    /**
     * checks if a given rectangle inside the field has only water in it (for positioning a ship)
     * @param rowFrom lower row index
     * @param rowTo upper row index
     * @param columnFrom lower column index
     * @param columnTo upper column index
     * @return the check result
     */
    private boolean isWaterInRectangle(int rowFrom, int rowTo, int columnFrom, int columnTo) {
        for (int row = rowFrom; row <= rowTo; row++) {
            for (int column = columnFrom; column <= columnTo; column++) {
                if (field[row][column] != BattlefieldCell.WATER) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks, if the ship fits exactly into the user given cell area
     * @param indices the indices converted from the user input
     * @param ship the ship to position
     * @return the check result, if the ship fits
     */
     boolean matchesShipLength(List<Indices> indices, Battleship ship) {
        if (ship.isRowAligned()
                && Math.abs(indices.get(0).column - indices.get(1).column) + 1 == ship.getCells()
            || !ship.isRowAligned()
                && Math.abs(indices.get(0).row - indices.get(1).row) + 1 == ship.getCells()) {
            return true;
        }
        log.error(String.format(PropertyManager.getProperty("error-msg-ship-length"), ship.getName()));
        return false;
    }

    /**
     * Checks, if the user given cell positions are in a row or a column (= aligned).
     * SIDE EFFECT: Only in case the check succeeds, the ships alignment and position fields are updated.
     * @param indices the indices converted from the user input
     * @param ship the ship to position
     * @return the check result
     */
     boolean positionsAreAlignedOnField(List<Indices> indices, Battleship ship) {
        if (indices.get(0).row == indices.get(1).row) {
            ship.setRowAligned(true);
            ship.setRow(indices.get(0).row);
            ship.setColumn(Math.min(indices.get(0).column, indices.get(1).column));
            return true;
        }
        if (indices.get(0).column == indices.get(1).column) {
            ship.setRowAligned(false);
            ship.setRow(Math.min(indices.get(0).row, indices.get(1).row));
            ship.setColumn(indices.get(0).column);
            return true;
        }
        log.error(PropertyManager.getProperty("error-msg-ship-location"));
        return false;
    }
}