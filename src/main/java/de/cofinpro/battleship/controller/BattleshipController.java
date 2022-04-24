package de.cofinpro.battleship.controller;

import de.cofinpro.battleship.config.PropertyManager;
import de.cofinpro.battleship.model.Battleship;
import de.cofinpro.battleship.model.Shot;
import de.cofinpro.battleship.model.ShotResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Controller class - application logic which controls the game workflow of the battleship game.
 * It creates a queue of 2 players, which in turns set up their battlefields and shoot in a
 * play loop until one player wins.
 */
@Slf4j
public class BattleshipController {

    private final Queue<UserSession> players = new ArrayDeque<>();

    /**
     * the run method of the game - entry point for main program.
     * Initializes the player sessions, lets both players position their fleet and starts the play loop.
     */
    public void run() {
        printPropertiesInfo(PropertyManager.getProperties());
        initPlayerSessions();
        nextUserPositionShips();
        nextUserPositionShips();
        play();
    }

    /**
     * create the User sessions for both players and add them to the players queue.
     */
    private void initPlayerSessions() {
        UserSession player1 = new UserSession("Player 1");
        UserSession player2 = new UserSession("Player 2");
        players.offer(player1);
        players.offer(player2);
    }

    /**
     * takes out the player waiting at the head of the players queue and lets him position his ships.
     * After that puts him back to the end of the queue.
     */
    private void nextUserPositionShips() {
        UserSession player = players.remove();
        player.userAddOwnShipsToBattleField();
        players.offer(player);
    }

    /**
     * play loop - until all ships are sunk. The currentPlayer is taken from the head of the queue, while
     * the element() call peeks the opponent. The opponents battlefield is only shown obscured.
     * The player who has the turn shoots and the opponent applies the shot to his battlefield.
     * Then the turn changes - until one player has won.
     */
    void play() {
        log.info("\nThe game starts!");
        ShotResult shotResult = ShotResult.NONE;
        while (shotResult != ShotResult.WON) {
            UserSession currentPlayer = players.remove();
            UserSession opponent = players.element();

            opponent.getBattlefieldUI().displayBattlefieldObscured();
            currentPlayer.getBattlefieldUI().displayBattlefield();
            shotResult = opponent.applyShot(currentPlayer.shoots());
            players.offer(currentPlayer);
        }
        log.info(PropertyManager.getProperty("msg-win"));
    }

    /**
     * display all property settings to the user (on trace level).
     * @param properties the Properties hashmap of all application properties
     */
    private void printPropertiesInfo(Properties properties) {
        log.trace("Application parameters set as follows:");
        properties.keySet().stream()
                .sorted()
                .forEach(key -> log.trace(key + " = " + properties.getProperty((String) key)));
    }
}
