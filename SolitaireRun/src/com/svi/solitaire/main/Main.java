package com.svi.solitaire.main;

import java.util.Scanner;

import com.svi.solitaire.logic.DeckInitializer;
import com.svi.solitaire.resources.GameInfo;
import com.svi.solitaire.utilities.GameMenu;

/**
 * The {@code Main} class serves as the entry point for the Klondike Solitaire game.
 * It initializes key game components, such as the game information, deck, and menu,
 * and starts the main menu of the game.
 * <p>
 * This class demonstrates a modular structure by delegating functionality to
 * other classes for deck initialization, game information, and menu handling.
 * </p>
 * 
 * @author Emmanuel Simbulan
 */
public class Main {

    /**
     * The main method is the entry point of the application.
     * It initializes and manages the lifecycle of key game components.
     * 
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Scanner to handle user input
        Scanner userInput = new Scanner(System.in);

        try {
            // Initialize the game components
            GameInfo about = new GameInfo(userInput); // Display game information
            DeckInitializer deckInitializer = new DeckInitializer(); // Initialize the deck
            GameMenu menu = new GameMenu(userInput, about, deckInitializer); // Handle game menus

            // Start the main menu
            menu.start();
        } finally {
            userInput.close(); // Ensure scanner is closed to release resources
        }
    }
}
