package de.cofinpro.battleship.controller;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battlefield;
import de.cofinpro.battleship.model.Battleship;
import de.cofinpro.battleship.model.Shot;
import de.cofinpro.battleship.model.ShotResult;
import de.cofinpro.battleship.view.BattlefieldCell;
import de.cofinpro.battleship.view.BattlefieldUI;
import de.cofinpro.battleship.view.CommandLineUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * class that represents the gaming tools, that each of the player has to his service. In the game,
 * there is one instance of this class for both of the players.
 */
public class UserSession {

    private final String userName;
    private CommandLineUI commandLineUI = new CommandLineUI();
    private Battlefield battlefield;
    private List<Battleship> fleet;
    private BattlefieldUI battlefieldUI;

    public UserSession(String userName) {
        this.userName = userName;
        battlefield = initBattleField();
        fleet = initFleet();
    }

    public BattlefieldUI getBattlefieldUI() {
        return battlefieldUI;
    }

    /**
     * init the battlefield and the attached battlefieldUI with configurable size.
     */
    Battlefield initBattleField() {
        int size = Integer.parseInt(PropertyManager.getProperty("field-size"));
        if (size < 2 || size > 26) {
            throw new ApplicationPropertiesException("Field-size property value must be in [2,26]. Given: " + size);
        }
        battlefield = new Battlefield(size);
        battlefieldUI = new BattlefieldUI(battlefield);
        return battlefield;
    }

    /**
     * initialize the highly configurable fleet of battleships.
     * @return the fleet as List<Battleship>.
     */
    List<Battleship> initFleet() {
        fleet = new ArrayList<>();
        List<String> shipNames = Arrays.stream(PropertyManager.getProperty("ship-names").split(",")).toList();
        fleet.addAll(getNCellsShips(5, "five-cell-ships", shipNames, 0));
        fleet.addAll(getNCellsShips(4, "four-cell-ships", shipNames, fleet.size()));
        fleet.addAll(getNCellsShips(3, "three-cell-ships", shipNames, fleet.size()));
        fleet.addAll(getNCellsShips(2, "two-cell-ships", shipNames, fleet.size()));
        if (fleet.size() != shipNames.size()) {
            commandLineUI.warn("Application properties mismatch: more ship names specified as ships requested");
        }
        commandLineUI.trace(fleet.toString());
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
     * user positioning loop over all ships in the fleet.
     */
    public void userAddOwnShipsToBattleField() {
        commandLineUI.info(String.format(PropertyManager.getProperty("msg-place-ships"), userName));
        battlefieldUI.displayBattlefield();
        fleet.forEach(this::userPositionShip);
        commandLineUI.promptForPlayerChange();
    }

    /**
     * method to ask the user for the position of a given ship in a loop until it fits on the field.
     * @param battleship the ship to position
     */
    void userPositionShip(Battleship battleship) {
        List<String> positions;
        do {
            positions = commandLineUI.promptForShipPosition(battleship.getName(), battleship.getCells());
        } while (!battlefield.couldPositionShip(positions, battleship));
        battlefieldUI.displayBattlefield();
    }

    /**
     * method that keeps prompting the user for coordinates of the next shot until the given coordinates are
     * valid.
     * @return immutable Shot position object corresponding to the user entry.
     */
    public Shot shoots() {
        commandLineUI.info(String.format(PropertyManager.getProperty("msg-shot"), userName));
        Optional<Shot> shot;
        do {
            String positionToken = commandLineUI.promptForShotPosition();
            shot = battlefield.getShot(positionToken).or(()-> {
                commandLineUI.error(PropertyManager.getProperty("error-msg-wrong-coords"));
                return Optional.empty();
            });
        } while (shot.isEmpty());
        return shot.get();
    }

    /**
     * applies the shot position given to the battlefield, that is updated accordingly,
     * i.e. ships store a possible cell-hit and are even removed, when their last cell is hit
     * and missed shots are marked. The shot result is printed and a playerChange is initiated
     * unless the shot leads to a player's win.
     * @param shot the shot position to apply
     * @return a ShotResult Enum type
     */
    public ShotResult applyShot(Shot shot) {
        BattlefieldCell cell = battlefield.getField()[shot.getRow()][shot.getColumn()];
        if (cell == BattlefieldCell.WATER || cell == BattlefieldCell.MISS) {
            battlefield.setCell(shot.getRow(), shot.getColumn(), BattlefieldCell.MISS);
            commandLineUI.info(PropertyManager.getProperty("msg-miss"));
            commandLineUI.promptForPlayerChange();
            return ShotResult.MISSED;
        }

        battlefield.setCell(shot.getRow(), shot.getColumn(), BattlefieldCell.HIT);
        Battleship sunkShip = shipSunkByHit(fleet, shot);
        if (sunkShip == null) {
            commandLineUI.info(PropertyManager.getProperty("msg-hit"));
            commandLineUI.promptForPlayerChange();
            return ShotResult.HIT;
        }

        fleet.remove(sunkShip);
        if (!fleet.isEmpty()) {
            commandLineUI.info(PropertyManager.getProperty("msg-sink"));
            commandLineUI.promptForPlayerChange();
            return ShotResult.SUNK;
        }
        return ShotResult.WON;
    }

    /**
     * applies a hit to a ship and returns, if the ship has sunk, i.e. the last cell was hit. To achieve this check,
     * the Battleship stores the hit cell.
     * @param fleet the fleet to check for a hit
     * @param shot the shot to apply
     * @return the Battleship that has just sunk, or null, if no ship sunk by this shot
     */
    Battleship shipSunkByHit(List<Battleship> fleet, Shot shot) {
        for (Battleship ship: fleet) {
            if (ship.hitsShip(shot.getRow(), shot.getColumn()) && ship.remainingCells() == 0) {
                return ship;
            }
        }
        return null;
    }
}