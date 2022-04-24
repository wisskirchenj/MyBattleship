package de.cofinpro.battleship.model;

import lombok.Value;

/**
 * simple immutable shot class, that stores the position of a shot as row, column indices.
 */
@Value
public class Shot {

    int row;
    int column;
}
