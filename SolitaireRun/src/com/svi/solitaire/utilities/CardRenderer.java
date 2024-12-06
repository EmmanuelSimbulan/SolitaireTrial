package com.svi.solitaire.utilities;

import com.svi.solitaire.vo.Card;
import com.svi.solitaire.logic.TurnModeHandler; // Import TurnModeHandler
import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;

/**
 * The {@code CardRenderer} class provides methods for rendering various elements
 * of the Klondike Solitaire game, including the tableau, foundation piles, and talon.
 * It outputs the current state of these elements to the console for display.
 */
public class CardRenderer {

    /**
     * Renders the tableau, displaying each column with cards.
     * Each column shows the face-up and face-down cards in proper alignment.
     * 
     * @param tableau A list of ArrayDeque objects representing the tableau columns,
     *                where each column holds a stack of {@link Card} objects.
     */
    public static void renderTableau(List<ArrayDeque<Card>> tableau) {
        System.out.println("\nTableau:");
        
        for (int columnIndex = 0; columnIndex < tableau.size(); columnIndex++) {
            System.out.print("Column " + (columnIndex + 1) + ": ");
            ArrayDeque<Card> column = tableau.get(columnIndex);
            
            if (column.isEmpty()) {
                System.out.print("[Empty]");
            } else {
                // Separate closed and open cards for proper alignment
                StringBuilder closedCards = new StringBuilder();
                StringBuilder openCards = new StringBuilder();

                for (Card card : column) {
                    if (card.isFaceUp()) {
                        openCards.append(card).append(" ");
                    } else {
                        closedCards.append("[XX] ");
                    }
                }

                // Print closed cards first, followed by open cards
                System.out.print(closedCards.toString());
                System.out.print(openCards.toString().trim());
            }
            System.out.println();
        }
    }

    /**
     * Renders the foundation piles, displaying each pile's cards.
     * 
     * @param foundation A list of ArrayDeque objects representing the foundation piles,
     *                  where each pile holds a stack of {@link Card} objects.
     */
    public static void renderFoundation(List<ArrayDeque<Card>> foundation) {
        System.out.println("\nFoundation Piles:");
        
        for (int pileIndex = 0; pileIndex < foundation.size(); pileIndex++) {
            System.out.print("Foundation " + (pileIndex + 1) + ": ");
            ArrayDeque<Card> pile = foundation.get(pileIndex);
            
            if (pile.isEmpty()) {
                System.out.print("[Empty]");
            } else {
                // Print all cards in the foundation pile
                for (Card card : pile) {
                    System.out.print(card + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Renders the talon pile based on the turn mode from the {@link TurnModeHandler}.
     * Displays either one or three cards depending on the current turn mode.
     * 
     * @param talon The talon pile, represented as an {@link ArrayDeque} of {@link Card} objects.
     * @param turnModeHandler The {@link TurnModeHandler} that determines whether cards are dealt in turn 1 or turn 3 mode.
     */
    public static void renderTalon(ArrayDeque<Card> talon, TurnModeHandler turnModeHandler) {
        System.out.print("\nTalon Card: ");
        
        if (talon.isEmpty()) {
            System.out.print("[Empty]");
        } else {
            TurnModeHandler.TurnMode currentTurnMode = turnModeHandler.getTurnMode(); // Get the current turn mode as enum

            if (currentTurnMode == TurnModeHandler.TurnMode.TURN_1) {  // Turn 1 mode
                Card topCard = talon.peek();  // Get the top card from the talon
                System.out.print(topCard + " ");
            } else if (currentTurnMode == TurnModeHandler.TurnMode.TURN_3) {  // Turn 3 mode
                // Indicate more cards are available by printing '[...]'
                System.out.print("[...] ");

                // Render the first 3 cards in the talon
                int cardCount = 0;
                ArrayDeque<Card> remainingCards = new ArrayDeque<>(talon);

                // Collect the last 3 cards in reverse order
                Stack<Card> lastThreeCards = new Stack<>();
                while (cardCount < 3 && !remainingCards.isEmpty()) {
                    Card talonCard = remainingCards.poll();  // Get the next card
                    lastThreeCards.push(talonCard);  // Push it to a stack (reverse order)
                    cardCount++;
                }

                // Print the last 3 cards in reverse order (since Stack pops in reverse order)
                while (!lastThreeCards.isEmpty()) {
                    System.out.print(lastThreeCards.pop() + " ");  // Print the cards in reverse order
                }
            }
        }
        System.out.println();
    }
}
