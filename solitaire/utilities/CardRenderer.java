package com.svi.solitaire.utilities;

import com.svi.solitaire.vo.Card;
import com.svi.solitaire.logic.TurnModeHandler; // Import TurnModeHandler
import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;

public class CardRenderer {

    // Render the tableau
    public static void renderTableau(List<ArrayDeque<Card>> tableau) {
        System.out.println("\nTableau:");
        for (int i = 0; i < tableau.size(); i++) {
            System.out.print("Column " + (i + 1) + ": ");
            ArrayDeque<Card> column = tableau.get(i);
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

    // Render the foundation piles
    public static void renderFoundation(List<ArrayDeque<Card>> foundation) {
        System.out.println("\nFoundation Piles:");
        for (int i = 0; i < foundation.size(); i++) {
            System.out.print("Foundation " + (i + 1) + ": ");
            ArrayDeque<Card> pile = foundation.get(i);
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

    // Render the talon pile based on the turn mode from TurnModeHandler
    public static void renderTalon(ArrayDeque<Card> talon, TurnModeHandler turnModeHandler) {
        System.out.print("\nTalon Card: ");
        if (talon.isEmpty()) {
            System.out.print("[Empty]");
        } else {
            TurnModeHandler.TurnMode turnMode = turnModeHandler.getTurnMode(); // Get the current turn mode as enum

            if (turnMode == TurnModeHandler.TurnMode.TURN_1) {  // Turn 1 mode
                Card talonCard = talon.peek();  // Get the top card from the talon
                System.out.print(talonCard + " ");
            } else if (turnMode == TurnModeHandler.TurnMode.TURN_3) {  // Turn 3 mode
                // First, print '[...]' to indicate there are more cards
                System.out.print("[...] ");

                // Render the first 3 cards in the talon
                int count = 0;
                ArrayDeque<Card> remainingCards = new ArrayDeque<>(talon);

                // Collect the last 3 cards in reverse order
                Stack<Card> lastThreeCards = new Stack<>();
                while (count < 3 && !remainingCards.isEmpty()) {
                    Card talonCard = remainingCards.poll();  // Get the next card
                    lastThreeCards.push(talonCard);  // Push it to a stack (reverse order)
                    count++;
                }

                // Now, print the last 3 cards in reverse order (since Stack pops in reverse order)
                while (!lastThreeCards.isEmpty()) {
                    System.out.print(lastThreeCards.pop() + " ");  // Print the cards in reverse order
                }
            }
        }
        System.out.println();
    }
}