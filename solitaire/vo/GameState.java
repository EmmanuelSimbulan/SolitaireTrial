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
        boolean updated = false;  // Flag to track if the game state was modified

        while (!gameOver) {
            boolean moved = false;
            
            System.out.println("\n============ Move " + (moves + 1) + " ============\n");

            // Try moving cards from tableau to foundation
            for (ArrayDeque<Card> column : tableau) {
                if (!column.isEmpty()) {
                    Card topCard = column.peek();

                    if (topCard.isFaceUp() && addToFoundation(topCard)) {
                        column.pop();  // Remove the card from tableau after moving it
                        moved = true;
                        moves++;
                        // Log the move
                        CardMovementHandler.logCardMovement(topCard, "Foundation", (foundation.indexOf(column) + 1));
                        updated = true;  // Mark that game state was updated
                        break;
                    }
                }
            }

            // Try moving cards from talon to tableau or foundation
            if (!moved && !talon.isEmpty()) {
                Card topCard = talon.peek();

                if (addToFoundation(topCard)) {
                    talon.pop();
                    moved = true;
                    moves++;
                    // Log the move
                    CardMovementHandler.logCardMovement(topCard, "Foundation", foundation.indexOf(topCard) + 1);
                    updated = true;
                } else {
                    for (ArrayDeque<Card> column : tableau) {
                        if (column.isEmpty() && topCard.getRank() == Rank.KING) {
                            column.push(talon.pop()); // Place King in empty tableau
                            column.peek().setFaceUp(true);
                            moved = true;
                            moves++;
                            // Log the move
                            CardMovementHandler.logCardMovement(topCard, "Column", tableau.indexOf(column) + 1);
                            updated = true;
                            break;
                        } else {
                            Card topColumnCard = column.peek();
                            // Ensure topColumnCard is not null before accessing its properties
                            if (topColumnCard != null && topColumnCard.getRank().ordinal() == topCard.getRank().ordinal() + 1 &&
                                topColumnCard.getSuit().getColor() != topCard.getSuit().getColor()) {
                                column.push(talon.pop()); // Add to tableau
                                column.peek().setFaceUp(true);
                                moved = true;
                                moves++;
                                // Log the move
                                CardMovementHandler.logCardMovement(topCard, "Column", tableau.indexOf(column) + 1);
                                updated = true;
                                break;
                            }
                        }
                    }
                }
            }

            // Redealing cards from talon to waste or from waste to talon
            if (!moved && !talon.isEmpty()) {
                if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_1) {
                    Card talonCard = talon.pop();  // Take the top card from talon
                    waste.push(talonCard);         // Move to waste
                    moves++;  // Increment move count
                    System.out.println("Card " + talonCard + " is moved to the Waste Pile.");
                } else if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_3) {
                    for (int i = 0; i < 3 && !talon.isEmpty(); i++) {
                        Card talonCard = talon.pop();  // Take the next card from talon
                        waste.push(talonCard);         // Move to waste
                        moves++;  // Increment move count
                        System.out.println("Card " + talonCard + " is moved to the Waste Pile.");
                    }
                }
                updated = true; // Mark as updated if dealing occurred
            }

            if (!moved && talon.isEmpty() && !waste.isEmpty() && passesThroughTalon < 2) {
                System.out.println("Redealing cards from waste to talon...");
                redealTalon(); // Redeal cards
                updated = true;  // Update the game state after redealing
            }

            // Check if the game is won
            if (CardMovementHandler.validateGameWon(foundation)) {
                gameOver = true;
                System.out.println("Congratulations! You won!");
            }

            // Update game state if necessary
            if (updated) {
                printGameStatus();
                updateGameState();
                updated = false; // Reset the updated flag after the game state is updated
            }
        }

    }

    // Redeals cards from waste to talon and resets the waste
    private void redealTalon() {
        // Redealing one card from talon to waste or multiple if Turn 3
        if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_1) {
            Card talonCard = talon.pop();  // Take the top card from talon
            waste.push(talonCard);         // Move to waste
            System.out.println("The talon card " + talonCard + " is moved to the waste pile.");
        } else if (turnModeHandler.getTurnMode() == TurnModeHandler.TurnMode.TURN_3) {
            for (int i = 0; i < 3 && !talon.isEmpty(); i++) {
                Card talonCard = talon.pop();  // Take the next card from talon
                waste.push(talonCard);         // Move to waste
            }
            System.out.println("Three cards from talon have been moved to the waste pile.");
        }
        passesThroughTalon++; // Increment the passes-through counter
        // Re-shuffle the waste cards back into talon
        if (passesThroughTalon < 2) {
            talon.addAll(waste);
            waste.clear(); // Clear the waste pile after the redeal
            CardRenderer.renderTalon(talon, turnModeHandler); // Re-render the talon
        }
    }

    // Checks and returns true if the card can be added to the foundation
    public boolean addToFoundation(Card card) {
        if (card.getRank() == Rank.ACE) {
            int foundationIndex = card.getSuit().ordinal();
            foundation.get(foundationIndex).push(card);
            return true;
        } else {
            int foundationIndex = card.getSuit().ordinal();
            if (!foundation.get(foundationIndex).isEmpty()) {
                Card topFoundationCard = foundation.get(foundationIndex).peek();
                if (topFoundationCard.getRank().ordinal() + 1 == card.getRank().ordinal()) {
                    foundation.get(foundationIndex).push(card);
                    return true;
                }
            }
        }
        return false;
    }

    // Update and render the game state
    public void updateGameState() {
        CardRenderer.renderTableau(tableau);
        CardRenderer.renderFoundation(foundation);
        CardRenderer.renderTalon(talon, turnModeHandler);
    }

    // Print the game status after every move
    public void printGameStatus() {
        System.out.println("\nMoves: " + moves);
        System.out.println("Talon Size: " + talon.size());
        System.out.println("Passthrus: " + passesThroughTalon);
    }
}