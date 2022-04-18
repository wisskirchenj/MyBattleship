package de.cofinpro.battleship.view;

import de.cofinpro.battleship.config.PropertyManager;

/**
 * enumeration type which is the building block of a battlefield cell.
 * It gives its printable Symbol-String by the method getFieldSymbol(), which is a configurable property.
 */
public enum BattlefieldCell {
    WATER(PropertyManager.getProperty("water-symbol")),
    SHIP(PropertyManager.getProperty("own-ship-symbol")),
    HIT(PropertyManager.getProperty("hit-symbol")),
    MISS(PropertyManager.getProperty("miss-symbol"));
    private String cellSymbol;

    BattlefieldCell(String symbol) {
        this.cellSymbol = symbol;
    }

    public String getCellSymbol() {
        return cellSymbol;
    }

    void setCellSymbol(String newCellSymbol) {
        this.cellSymbol = newCellSymbol;
    }
}
