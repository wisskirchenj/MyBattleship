package de.cofinpro.battleship.view;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battlefield;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * UI-class for displaying the view representation of the model class Battlefield.
 */
@Slf4j
public class BattlefieldUI {

    private final Battlefield battlefield;
    private final String[] rowTitles;
    private final String[] columnTitles;

    public BattlefieldUI(Battlefield battlefield) {
        this.battlefield = battlefield;
        int size = battlefield.getSize();
        rowTitles = new String[size];
        columnTitles = new String[size];
        IntStream.range(0, size).forEach(n -> rowTitles[n] = String.format("%-2d", n + 1));
        IntStream.range(0, size).forEach(n -> columnTitles[n] = String.format("%-2c", 'A' + n));
    }

    /**
     * display the battlefield without showing the ships. The ship's position are displayed as water,
     * which is achieved by setting the ship's cellSymbol to water, calling the usual display method
     * and resetting afterwards.
     * @return the printed string - mainly for testing purpose
     */
    public String displayBattlefieldObscured() {
        BattlefieldCell.SHIP.setCellSymbol(PropertyManager.getProperty("water-symbol"));
        String obscuredDisplay = displayBattlefield();
        BattlefieldCell.SHIP.setCellSymbol(PropertyManager.getProperty("own-ship-symbol"));
        return obscuredDisplay;
    }

    /**
     * display the battlefield using the String cellSymbols of the field's Cell, an enum type.
     * @return the printed string - mainly for testing purpose
     */
    public String displayBattlefield() {
        BattlefieldCell[][] field = battlefield.getField();
        StringBuilder builder = new StringBuilder("\n  ");
        builder.append(String.join("", rowTitles));
        for (int i = 0; i < battlefield.getSize(); i++) {
            builder.append("\n").append(columnTitles[i]);
            for (int j = 0; j < battlefield.getSize(); j++) {
                builder.append(field[i][j].getCellSymbol());
            }
        }
        log.info(builder.toString());
        return builder.toString();
    }
}
