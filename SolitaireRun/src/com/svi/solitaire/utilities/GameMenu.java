package com.svi.solitaire.utilities;

import java.util.ArrayDeque;
import java.util.Scanner;
import com.svi.solitaire.resources.GameInfo;
import com.svi.solitaire.logic.DeckInitializer;
import com.svi.solitaire.logic.ShuffleManager;
import com.svi.solitaire.logic.TurnModeHandler;
import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.GameState;

/**
 * The GameMenu class represents the main menu and game flow logic for Klondike Solitaire.
 * It manages the interaction with the user, including selecting turn modes, shuffling types,
 * and handling game settings and outcomes.
 */
public class GameMenu {
    private Scanner userInput;
    private GameInfo about;
    private DeckInitializer deckInitializer;
    private TurnModeHandler turnModeHandler;
    private boolean shuffleDisplaySetting; // Setting to control shuffle display
    private GameState gameState; // GameState instance

    /**
     * Constructs a GameMenu instance with the specified user input, game information, and deck initializer.
     *
     * @param userInput      The scanner for reading user input.
     * @param about          The GameInfo instance for displaying about menu.
     * @param deckInitializer The DeckInitializer instance for initializing the deck.
     */
    public GameMenu(Scanner userInput, GameInfo about, DeckInitializer deckInitializer) {
        this.userInput = userInput;
        this.about = about;
        this.deckInitializer = deckInitializer;
        this.turnModeHandler = new TurnModeHandler();
        this.shuffleDisplaySetting = false; // Default is off
        this.gameState = new GameState(); // Initialize GameState without TurnModeHandler
    }

    /**
     * Starts the game by displaying the main menu and processing user input to initiate the game.
     */
    public void start() {
        boolean running = true;
        while (running) {
            int userChoice = displayMainMenu();
            switch (userChoice) {
                case 1:
                    playGame(); // Handles one game per initialization
                    break;
                case 2:
                    about.displayAboutMenu();
                    break;
                case 3:
                    displaySettingsMenu();
                    break;
                case 4:
                    running = false;
                    System.out.println("\nGoodbye!");
                    break;
                default:
                    System.out.println("\nInvalid Option. Please Try Again!");
            }
        }
    }

    /**
     * Starts a new game, handles turn mode, shuffle type, and deck initialization.
     */
    private void playGame() {
        int turnMode = displayPlayMenu(); // Get the selected turn mode from the play menu
        if (turnMode != -1) { // Valid turn mode selected
            turnModeHandler.setTurnMode(turnMode); // Set the turn mode dynamically

            // Set the turn mode in the game state
            gameState.setTurnMode(turnMode); // Set turn mode in game state

            int shuffleChoice = displayShuffleMenu(turnMode); // Pass turn mode to shuffle menu
            if (shuffleChoice != -1) { // Valid shuffle option selected
                ArrayDeque<Card> shuffledDeck = initializeShuffle(turnMode, shuffleChoice);

                // Validate deck size before distributing cards
                if (shuffledDeck.size() != 52) {
                    throw new IllegalStateException("Shuffled deck contains " + shuffledDeck.size() + " cards. Expected 52 cards.");
                }

                // Print the shuffled deck if shuffle display is enabled
                handleShuffledDeck(shuffledDeck);

                // Reset the game state before distributing new cards
                gameState.resetGameState();
                ShuffleManager.handleShuffledDeck(shuffledDeck, gameState); // Handle distribution of cards

                // After the game, automatically play the game
                gameState.autoPlayGame(); // Start auto-play

                // After the game, prompt the user for further action
                displayPostGameMenu();
            }
        }
    }

    /**
     * Displays the post-game menu and handles user input for replaying or exiting.
     */
    private void displayPostGameMenu() {
        boolean inPostGameMenu = true;
        while (inPostGameMenu) {
            String prompt = "\n========= GAME COMPLETED =========\n"
                    + "(1) PLAY AGAIN (Return to Main Menu)\n"
                    + "(2) EXIT";
            int userChoice = getValidInput(prompt, 1, 2);

            switch (userChoice) {
                case 1:
                    inPostGameMenu = false; // Return to main menu
                    break;
                case 2:
                    System.out.println("\nGoodbye!");
                    System.exit(0); // Exit the application
                    break;
                default:
                    System.out.println("\nInvalid Option. Please Try Again!");
            }
        }
    }

    /**
     * Displays the main menu and prompts the user for an option.
     *
     * @return The selected option as an integer.
     */
    public int displayMainMenu() {
        String prompt = "\n============== MAIN MENU ==============\n"
                + "(1) PLAY [KLONDIKE SOLITAIRE]\n"
                + "(2) ABOUT [KLONDIKE SOLITAIRE]\n"
                + "(3) SETTINGS\n"
                + "(4) EXIT";
        return getValidInput(prompt, 1, 4);
    }

    /**
     * Displays the play menu for selecting the turn mode (Turn 1 or Turn 3).
     *
     * @return The selected turn mode as an integer.
     */
    public int displayPlayMenu() {
        String prompt = "\n========== KLONDIKE SOLITAIRE ==========\n"
                + "(1) Klondike Turn 1\n"
                + "(2) Klondike Turn 3\n"
                + "(3) RETURN BACK";
        int userChoice = getValidInput(prompt, 1, 3);

        switch (userChoice) {
            case 1:
                return 1; // Set Turn 1
            case 2:
                return 3; // Set Turn 3
            case 3:
                return -1; // Indicate return to main menu
            default:
                return -1; // Fallback to main menu
        }
    }

    /**
     * Displays the shuffle menu for selecting the shuffle type (In-Faro, Out-Faro, etc.).
     *
     * @param turnMode The selected turn mode.
     * @return The selected shuffle type as an integer.
     */
    public int displayShuffleMenu(int turnMode) {
        String prompt = "\n============ KLONDIKE TURN " + turnMode + " ============\n"
                + "(1) In-Faro\n"
                + "(2) Out-Faro Shuffle\n"
                + "(3) Normal Shuffle (Random)\n"
                + "(4) Hard Shuffle (Random)\n"
                + "(5) RETURN BACK";
        int userChoice = getValidInput(prompt, 1, 5);

        switch (userChoice) {
            case 1: case 2: case 3: case 4:
                return userChoice; // Valid shuffle option
            case 5:
                return -1; // Return back
            default:
                return -1; // Fallback
        }
    }

    /**
     * Initializes the deck and applies the selected shuffle.
     *
     * @param turnMode   The selected turn mode (Turn 1 or Turn 3).
     * @param shuffleChoice The selected shuffle type.
     * @return A shuffled deck as an ArrayDeque of cards.
     */
    private ArrayDeque<Card> initializeShuffle(int turnMode, int shuffleChoice) {
        ArrayDeque<Card> currentDeck = new ArrayDeque<>(deckInitializer.getDeck()); // Convert to ArrayDeque

        switch (shuffleChoice) {
            case 1:
                System.out.println("Starting In-Faro Shuffle for Turn " + turnMode + "...\n");
                currentDeck = ShuffleManager.inFaroShuffle(currentDeck);
                break;
            case 2:
                System.out.println("Starting Out-Faro Shuffle for Turn " + turnMode + "...\n");
                currentDeck = ShuffleManager.outFaroShuffle(currentDeck);
                break;
            case 3:
                System.out.println("Starting Normal Shuffle for Turn " + turnMode + "...\n");
                currentDeck = ShuffleManager.normalShuffle(currentDeck);
                break;
            case 4:
            	System.out.println("Starting Hard Shuffle for Turn " + turnMode + "...\n");
                currentDeck = ShuffleManager.hardShuffle(currentDeck);
                break;
        }

        return currentDeck;
    }

    /**
     * Displays the settings menu for adjusting game settings such as shuffle display.
     */
    public void displaySettingsMenu() {
        boolean inSettingsMenu = true;
        while (inSettingsMenu) {
            String prompt = "\n============= SETTINGS =============\n"
                    + "(1) Show Shuffle Deck [ON/OFF]\n"
                    + "(2) RETURN BACK";
            int userChoice = getValidInput(prompt, 1, 2);

            switch (userChoice) {
                case 1:
                    toggleShuffleDisplaySetting();
                    break;
                case 2:
                    inSettingsMenu = false;
                    break;
            }
        }
    }

    /**
     * Toggles the display setting for showing the shuffled deck.
     */
    public void toggleShuffleDisplaySetting() {
        shuffleDisplaySetting = !shuffleDisplaySetting; // Toggle between true and false
        String status = shuffleDisplaySetting ? "ON" : "OFF";
        System.out.println("\nShuffle Deck Display is now " + status);
    }

    /**
     * Prints the shuffled deck if the shuffle display setting is enabled.
     *
     * @param shuffledDeck The shuffled deck of cards.
     */
    private void handleShuffledDeck(ArrayDeque<Card> shuffledDeck) {
        if (shuffleDisplaySetting) { // Check if shuffle display is on
            System.out.println("Shuffled Deck:");
            shuffledDeck.forEach(card -> System.out.print(card + " "));
            System.out.println(); // Newline after printing the deck
        }
    }

    /**
     * Prompts the user for valid input between the specified min and max values.
     *
     * @param prompt The message to display to the user.
     * @param min    The minimum valid input value.
     * @param max    The maximum valid input value.
     * @return The valid user input.
     */
    private int getValidInput(String prompt, int min, int max) {
        int userChoice = -1;
        boolean valid = false;

        while (!valid) {
            System.out.println(prompt);
            if (userInput.hasNextInt()) {
                userChoice = userInput.nextInt();
                if (userChoice >= min && userChoice <= max) {
                    valid = true;
                } else {
                    System.out.println("\nInvalid Option. Please Try Again!");
                }
            } else {
                System.out.println("\nInvalid Option. Please Try Again!");
                userInput.next(); // Consume invalid input
            }
        }

        return userChoice;
    }
}
