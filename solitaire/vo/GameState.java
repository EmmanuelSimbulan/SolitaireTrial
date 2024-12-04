package com.svi.solitaire.vo;

import com.svi.solitaire.utilities.CardRenderer;
import com.svi.solitaire.logic.CardMovementHandler;
import com.svi.solitaire.logic.TurnModeHandler;

import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class GameState {
    private List<ArrayDeque<Card>> tableau; // 7 tableau columns
    private List<ArrayDeque<Card>> foundation; // 4 foundation piles
    private ArrayDeque<Card> talon; // Stock of undealt cards
    private ArrayDeque<Card> waste; // Cards dealt from the talon

    private TurnModeHandler turnModeHandler; // Instance of TurnModeHandler

    // Tracking fields
    private int moves; // Tracks the number of moves
    private int passesThroughTalon; // Tracks how many times the talon has been passed through
    private boolean isInitialStateRendered = false; // Track whether the initial game state has been rendered

    // Constructor now initializes the tracking fields and TurnModeHandler
    public GameState() {
        this.turnModeHandler = new TurnModeHandler(); // Initialize TurnModeHandler
        this.moves = 0; // Initialize move counter
        this.passesThroughTalon = 0; // Initialize passes through talon counter
        resetGameState(); // Keep TurnModeHandler intact, only reset other game state components
    }

    // Method to set the turn mode after the GameState is created
    public void setTurnMode(int turnMode) {
        this.turnModeHandler.setTurnMode(turnMode); // Set the turn mode dynamically
    }

    // Reset the entire game state to start a new game
    public void resetGameState() {
        tableau = new ArrayList<>(7); // Initialize 7 tableau columns
        for (int i = 0; i < 7; i++) {
            tableau.add(new ArrayDeque<>());
        }

        foundation = new ArrayList<>(4); // Initialize 4 foundation piles
        for (int i = 0; i < 4; i++) {
            foundation.add(new ArrayDeque<>());
        }

        talon = new ArrayDeque<>();
        waste = new ArrayDeque<>();
        // Reset moves and passes through talon
        this.moves = 0;
        this.passesThroughTalon = 0;
    }

    // Distribute cards into tableau and talon
    public void distributeCards(ArrayDeque<Card> shuffledDeck) {
        // Reset the game state if necessary
        CardMovementHandler.validateDeck(shuffledDeck); // Validate the shuffled deck

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = shuffledDeck.poll(); // Take the next card from the shuffled deck

                CardMovementHandler.validateCardDistribution(card); // Validate card distribution

                if (j == i) {
                    card.setFaceUp(true); // The top card in the column is face-up
                } else {
                    card.setFaceUp(false); // All other cards are face-down
                }
                tableau.get(i).add(card); // Add the card to the current tableau column
            }
        }

        talon.addAll(shuffledDeck); // Remaining cards go to the talon

        // Print the initial game state after distribution (rendering occurs only once)
        if (!isInitialStateRendered) {
            // Only render the game state once after cards are distributed
            CardRenderer.renderTableau(tableau);
            CardRenderer.renderFoundation(foundation);
            CardRenderer.renderTalon(talon, turnModeHandler); // Pass the TurnModeHandler instance to renderer
            isInitialStateRendered = true; // Mark the state as rendered
        }
    }

    // Auto-play the game by automatically making valid moves
    public void autoPlayGame() {
        boolean gameOver = false;

        while (!gameOver) {
            boolean updated = false;  // Track if the game state changes in this cycle
            boolean moved = false;    // Track if a valid move was made in this cycle

            System.out.println("\n============ Move " + (moves + 1) + " ============\n");

            // Try moving cards from tableau to foundation
            for (ArrayDeque<Card> column : tableau) {
                if (!column.isEmpty()) {
                    Card topCard = column.peek();

                    if (topCard.isFaceUp() && addToFoundation(topCard)) {
                        column.pop();  // Remove the card from tableau after moving it
                        moved = true;
                        updated = true;
                        moves++;
                        CardMovementHandler.logCardMovement(topCard, "Foundation", foundation.indexOf(column) + 1);
                        break;
                    }
                }
            }

            // Try moving cards from tableau to tableau
            for (ArrayDeque<Card> sourceColumn : tableau) {
                if (!sourceColumn.isEmpty()) {
                    Card topSourceCard = sourceColumn.peek();
                    for (ArrayDeque<Card> targetColumn : tableau) {
                        if (sourceColumn != targetColumn && canMoveToTableau(topSourceCard, targetColumn)) {
                            targetColumn.push(sourceColumn.pop());
                            topSourceCard.setFaceUp(true);
                            moved = true;
                            updated = true;
                            moves++;
                            CardMovementHandler.logCardMovement(topSourceCard, "Column", tableau.indexOf(targetColumn) + 1);
                            break;
                        }
                    }
                    if (moved) break;
                }
            }

            // Try moving cards from talon to tableau or foundation
            if (!moved && !talon.isEmpty()) {
                Card topCard = talon.peek();
                if (addToFoundation(topCard)) {
                    talon.pop();
                    moved = true;
                    updated = true;
                    moves++;
                    CardMovementHandler.logCardMovement(topCard, "Foundation", foundation.indexOf(topCard) + 1);
                } else {
                    for (ArrayDeque<Card> column : tableau) {
                        if (column.isEmpty() && topCard.getRank() == Rank.KING) {
                            column.push(talon.pop()); // Place King in empty tableau
                            column.peek().setFaceUp(true);
                            moved = true;
                            updated = true;
                            moves++;
                            CardMovementHandler.logCardMovement(topCard, "Column", tableau.indexOf(column) + 1);
                            break;
                        } else {
                            Card topColumnCard = column.peek();
                            if (topColumnCard != null &&
                                    topColumnCard.getRank().ordinal() == topCard.getRank().ordinal() + 1 &&
                                    topColumnCard.getSuit().getColor() != topCard.getSuit().getColor()) {
                                column.push(talon.pop()); // Add to tableau
                                column.peek().setFaceUp(true);
                                moved = true;
                                updated = true;
                                moves++;
                                CardMovementHandler.logCardMovement(topCard, "Column", tableau.indexOf(column) + 1);
                                break;
                            }
                        }
                    }
                }
            }

            // Deal cards from talon to waste if no moves were made
            if (!moved && !talon.isEmpty()) {
                if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_1) {
                    Card talonCard = talon.pop();
                    waste.push(talonCard);
                    System.out.println("Card " + talonCard + " is moved to the Waste Pile.");
                    updated = true;
                    moves++;
                } else if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_3) {
                    for (int i = 0; i < 3 && !talon.isEmpty(); i++) {
                        Card talonCard = talon.pop();
                        waste.push(talonCard);
                        System.out.println("Card " + talonCard + " is moved to the Waste Pile.");
                        updated = true;
                        moves++;
                    }
                }
            }

            // Redeal cards if talon is empty and waste is non-empty
            if (!moved && talon.isEmpty() && !waste.isEmpty() && passesThroughTalon < 2) {
                System.out.println("Redealing cards from waste to talon...");
                redealTalon();
                updated = true;
            }

            // Check if the game is won
            if (CardMovementHandler.validateGameWon(foundation)) {
                gameOver = true;
                System.out.println("Congratulations! You won!");
                return;
            }

            // If no updates occurred during this cycle, the game is over (no more moves)
            if (!updated) {
                System.out.println("No valid moves left. Game over!");
                gameOver = true;
            } else {
                // Update the game state
                printGameStatus();
                updateGameState();
            }
        }
    }

    // Redeals cards from waste to talon and resets the waste
    private void redealTalon() {
        if (!waste.isEmpty()) {
            // Move all cards from waste back to talon
            while (!waste.isEmpty()) {
                talon.push(waste.pop());
            }

            passesThroughTalon++; // Increment the passes-through counter

            System.out.println("All cards from the waste pile have been moved back to the talon.");

            // Re-render the talon
            CardRenderer.renderTalon(talon, turnModeHandler);
        } else {
            System.out.println("Cannot redeal: The waste pile is empty.");
        }
    }

    // Checks and returns true if the card can be added to the foundation
    public boolean addToFoundation(Card card) {
        if (card == null) {
            return false; // Null check for card
        }

        for (ArrayDeque<Card> foundationPile : foundation) {
            if (foundationPile.isEmpty() && card.getRank() == Rank.ACE) {
                foundationPile.push(card);
                return true;
            } else if (!foundationPile.isEmpty()) {
                Card topCard = foundationPile.peek();
                if (topCard.getSuit() == card.getSuit() &&
                        topCard.getRank().ordinal() + 1 == card.getRank().ordinal()) {
                    foundationPile.push(card);
                    return true;
                }
            }
        }
        return false;
    }

    // Check if a card can be moved to a tableau column based on Klondike Solitaire rules
    private boolean canMoveToTableau(Card card, ArrayDeque<Card> targetColumn) {
        if (targetColumn.isEmpty()) {
            // Only Kings can be placed on empty tableau columns
            return card.getRank() == Rank.KING;
        } else {
            Card topCard = targetColumn.peek();
            // Ensure descending rank and alternating colors
            return topCard.getRank().ordinal() == card.getRank().ordinal() + 1
                    && topCard.getSuit().getColor() != card.getSuit().getColor();
        }
    }

    // Prints the current game status
    public void printGameStatus() {
        CardRenderer.renderTableau(tableau);
        CardRenderer.renderFoundation(foundation);
        CardRenderer.renderTalon(talon, turnModeHandler);
    }

    // Updates the game state after each move
    private void updateGameState() {
        // Implement any additional state updates here if necessary
    	System.out.println("\nMoves: " + moves);
        System.out.println("Talon Size: " + talon.size());
        System.out.println("Passthrus: " + passesThroughTalon);
    }
}