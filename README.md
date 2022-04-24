# IDEA EDU Course ...

Implemented in the Java Developer Track of hyperskill.org's JetBrain Academy.

Purpose of doing this project, is just practising some more plain java and finally completing the
Java Beginners Track - where this is a graduate project.
The application is designed very configurable by application.properties. Also, a lot of Mockito functionality
is tried out and used for testing.

## Repository Contents

Beside the sources of main project tasks (5 stages), all relevant Topic-associated development tasks of the academy 
course path are included.

## Program description

Well-known traditional game Battleship for 2 players with highly configurable ship fleet and battlefield.
First the player set both up their battlefields in turn. Then in the play loop 
they aim at the coordinates of an obscured view (not showing the ships) of their opponents battlefield and get back a shot result 
that is marked in their obscured view. Also, they have a clear view of their own ship positions where the shot results of
the opponent is updated.
The play loop continues until one player has won by sinking all the opponent's ships.

Have fun!

## Project completion

Project was completed on 24.04.22.

## Progress

14.04.22 Project started. IDEA-setup and first repo.

15.04.22 Stage 1 completed, Properties management, many new Mockito-feature tests

16.04.22 Stage 2 completed, first play scene added, Optional used, complete test coverage, some refactoring

18.04.22 Stage 3 completed with tests, new BattlefieldUI to separate view from model better. Not much new functionality.

21.04.22 Stage 4 completed with tests, full one player game now, Battleship class gets logic.

24.04.22 Stage 5 completed with tests, full 2 player functionality, introducing UserSession players queue.

