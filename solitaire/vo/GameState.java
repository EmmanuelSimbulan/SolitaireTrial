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

            // 1. Try moving cards from tableau to foundation
            for (ArrayDeque<Card> column : tableau) {
                if (!column.isEmpty()) {
                    Card topCard = column.peekLast();

                    if (topCard.isFaceUp() && addToFoundation(topCard)) {
                        column.pollLast();  // Remove the card from tableau after moving it
                        
                     // Flip the next card in the column face-up, if any remain
                        if (!column.isEmpty()) {
                            column.peekLast().setFaceUp(true);
                        }
                     
                        moved = true;
                        updated = true;
                        moves++;
                        CardMovementHandler.logCardMovement(topCard, "Foundation", foundation.indexOf(column) + 1);
                        break;
                    }
                }
            }
			
            // Add this code 2. Trying moving cards from tableau to tableau


            // 3. Try moving cards from talon to tableau or foundation
            if (!moved && !talon.isEmpty()) {
                Card topCard = talon.peek();
				// this is already correct
                if (addToFoundation(topCard)) {
                    talon.pop();
                    moved = true;
                    updated = true;
                    moves++;
                    CardMovementHandler.logCardMovement(topCard, "Foundation", foundation.indexOf(topCard) + 1);
                } else {
                    for (ArrayDeque<Card> column : tableau) {
						// this is already correct
                        if (column.isEmpty() && topCard.getRank() == Rank.KING) {
                            column.push(talon.pop()); // Place King in empty tableau
                            column.peek().setFaceUp(true);
                            moved = true;
                            updated = true;
                            moves++;
                            CardMovementHandler.logCardMovement(topCard, "Column", tableau.indexOf(column) + 1);
                            break;
						// I guess this is wrong it should follow the klondike rules Cards must be placed in descending order in the tableau, starting from King down to Ace.When moving cards, they must interleave in colors (red and black), so a red card can only be placed on top of a black card and vice versa. 
                        } else {
                            Card topColumnCard = column.peekLast();
                            if (topColumnCard != null &&
                                    topColumnCard.getRank().ordinal() == topCard.getRank().ordinal() + 1 &&
                                    topColumnCard.getSuit().getColor() != topCard.getSuit().getColor()) {
                                column.addLast(talon.poll()); // Add to tableau
                                column.peekLast().setFaceUp(true);
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