package de.cofinpro.battleship.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Shot {

    Battlefield.Indices position;
    boolean missed;
}
