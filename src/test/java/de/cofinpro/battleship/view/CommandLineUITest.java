package de.cofinpro.battleship.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandLineUITest {

    @Mock
    Scanner scanner;

    @InjectMocks
    CommandLineUI scannerUI;

    @BeforeEach
    void setUp() {
    }

    @Test
    void when2Tokens_promptForShipPositionReturnsTokens() {
        String tokens = "token1 token2";
        when(scanner.nextLine()).thenReturn(tokens);
        assertEquals("token1", scannerUI.promptForShipPosition("test ship", 3).get(0));
        assertEquals("token2", scannerUI.promptForShipPosition("other ship", 5).get(1));
    }

    @Test
    void whenNot2Tokens_promptForShipPositionLoops() {
        String tokens = "token1 token2";
        when(scanner.nextLine()).thenReturn("token", "1 2 3", tokens);
        assertEquals("token2", scannerUI.promptForShipPosition("test ship", 3).get(1));
        verify(scanner, times(3)).nextLine();
    }
}