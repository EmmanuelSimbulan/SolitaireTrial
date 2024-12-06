package com.svi.solitaire.resources;

import java.util.Scanner;

/**
 * The {@code GameInfo} class provides information about the rules and gameplay of Klondike Solitaire.
 * It displays the details of the game, including the objective, rules, and strategies.
 * It also provides an option for the user to return to the main menu after viewing the information.
 */
public class GameInfo {

    /**
     * The {@code Scanner} object used to read user input from the console.
     */
    private Scanner userInput;

    /**
     * Constructs a {@code GameInfo} object with the specified {@code Scanner}.
     * 
     * @param userInput The {@code Scanner} object for reading user input.
     */
    public GameInfo(Scanner userInput) {
        this.userInput = userInput;
    }

    /**
     * Displays detailed information about the game Klondike Solitaire, including:
     * <ul>
     *     <li>The objective of the game.</li>
     *     <li>The gameplay rules.</li>
     *     <li>The difference between Turn 1 and Turn 3 dealing modes.</li>
     *     <li>Strategy tips for playing the game.</li>
     * </ul>
     * After displaying the information, it waits for the user to press Enter before returning to the main menu.
     */
    public void displayAboutMenu() {
        String prompt = "\nABOUT KLONDIKE SOLITAIRE:\n"
                        + "Klondike Solitaire is a classic card game that involves strategy, skill, and a bit of luck. It is played using a standard deck of 52 cards.\n"
                        + "\nOBJECTIVE:\n"
                        + "The goal of Klondike Solitaire is to move all cards to the foundation piles, sorted by suit in ascending order (from Ace to King).\n"
                        + "\nGAMEPLAY RULES:\n"
                        + "1. The game starts with 28 cards distributed into seven tableau columns. The first column has one card, the second has two, and so on.\n"
                        + "2. The remaining cards form the stock pile, which can be drawn upon.\n"
                        + "3. Only Kings or sequences starting with Kings can be moved to empty tableau columns.\n"
                        + "4. Cards on the tableau must alternate in color (red and black) and descend in rank (e.g., a black 7 can be placed on a red 8).\n"
                        + "5. The foundation piles must be built up by suit starting from Ace (e.g., Ace of Hearts, then 2 of Hearts, and so on).\n"
                        + "\nTURN 1 vs. TURN 3:\n"
                        + "1. **Klondike Turn 1:**\n"
                        + "   - In this mode, you draw one card at a time from the stock pile.\n"
                        + "   - This makes the game easier, as you have more control over which cards are available.\n"
                        + "2. **Klondike Turn 3:**\n"
                        + "   - In this mode, you draw three cards at a time from the stock pile.\n"
                        + "   - This adds a layer of complexity and increases the challenge, as only the top card of the three drawn is accessible.\n"
                        + "\nSTRATEGY TIPS:\n"
                        + "1. Always prioritize uncovering hidden cards in the tableau.\n"
                        + "2. Try to avoid filling empty tableau spaces unless you have a King ready to place.\n"
                        + "3. Plan moves carefully to avoid getting stuck!\n"
                        + "\nEnjoy playing Klondike Solitaire and sharpening your problem-solving skills!\n";
        
        // Print the game information to the console
        System.out.println(prompt);

        // Option to return to the Main Menu
        System.out.println("\nEnter any key to return to the Main Menu...");
        
        // Wait for user input before returning to the main menu
        if (userInput.hasNextLine()) {
            userInput.nextLine();
        }
        
        userInput.nextLine(); // Waits for the user to press Enter
    }
}
