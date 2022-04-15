package de.cofinpro.battleship.controller;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battlefield;
import de.cofinpro.battleship.model.Battleship;
import de.cofinpro.battleship.view.PrinterUI;
import de.cofinpro.battleship.view.ScannerUI;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class - application logic which controls the game workflow of the battleship game.
 * It creates and uses the battlefield as well as a simple printerUI and scannerUI to communicate with the user.
 */
@Slf4j
public class BattleshipController {

    private final PrinterUI printer = new PrinterUI();
    private ScannerUI scanner = new ScannerUI();
    private Battlefield battlefield;

    /**
     * the run loop of the game - entry point for main program.
     * Initializes battlefield the fleet, asks the user to position the fleet and starts the game.
     */
    public void run() {
        log.info("Application started:-)");
        initBattleField();

        printer.printPropertiesInfo(PropertyManager.getProperties());
        List<Battleship> fleet = initFleet();
        userPositionOwnShips(fleet);
        play();
    }

    /**
     * init the battlefield with configurable size
     */
     void initBattleField() {
        int size = Integer.parseInt(PropertyManager.getProperty("field-size"));
        if (size < 2 || size > 26) {
            throw new ApplicationPropertiesException("Field-size property value must be in [2,26]. Given: " + size);
        }
        battlefield = new Battlefield(size);
        log.info("Battlefield initialized, size " + size + ":\n" + battlefield);
    }

    /**
     * initialize the highly configurable fleet of battleships.
     * @return the fleet as List<Battleship>.
     */
     List<Battleship> initFleet() {
        List<Battleship> fleet = new ArrayList<>();
        List<String> shipNames = Arrays.stream(PropertyManager.getProperty("ship-names").split(",")).toList();
        fleet.addAll(getNCellsShips(5, "five-cell-ships", shipNames, 0));
        fleet.addAll(getNCellsShips(4, "four-cell-ships", shipNames, fleet.size()));
        fleet.addAll(getNCellsShips(3, "three-cell-ships", shipNames, fleet.size()));
        fleet.addAll(getNCellsShips(2, "two-cell-ships", shipNames, fleet.size()));
        if (fleet.size() != shipNames.size()) {
            log.warn("Application properties mismatch: more ship names specified as ships requested");
        }
        log.trace(fleet.toString());
        return fleet;
    }

    /**
     * helper method during fleet initialization, that creates and returns all ships of a given Cell-length
     * @param cells the cells length
     * @param amountKey amount of ships in this category as configurable
     * @param shipNames the list af all ship names read from property.
     * @param namesOffset offset within the names list for this category
     * @return the list of ships created
     */
     List<Battleship> getNCellsShips(int cells, String amountKey, List<String> shipNames, int namesOffset) {
        List<Battleship> nCellsShips = new ArrayList<>();
        int shipsInCategory = Integer.parseInt(PropertyManager.getProperty(amountKey));

        if (namesOffset + shipsInCategory > shipNames.size()) {
            throw new ApplicationPropertiesException("not enough ship names specified as required for requested ships!");
        }
        for (int i = 0; i < shipsInCategory; i++, namesOffset++) {
            nCellsShips.add(new Battleship(shipNames.get(namesOffset), cells));
        }
        return nCellsShips;
    }

    /**
     * upcoming play loop
     */
    private void play() {
        log.info("playing...");
    }

    /**
     * user positioning loop over all ships in the fleet
     * @param fleet the fleet after creation
     */
    private void userPositionOwnShips(List<Battleship> fleet) {
        fleet.forEach(this::getValidUserPosition);
    }

    /**
     * method to ask the user for the position of a given ship in a loop until it fits on the field.
     * @param battleship the ship to position
     */
     void getValidUserPosition(Battleship battleship) {
        List<String> positions;
        do {
            positions = scanner.promptForShipPosition(battleship.getName(), battleship.getCells());
        } while (!battlefield.couldPositionShip(positions, battleship));
        printer.info(battlefield.toString());
    }
}
