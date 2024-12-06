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
    
    private List<Card> getMovableSequence(ArrayDeque<Card> column) {
        List<Card> sequence = new ArrayList<>();
        Card previousCard = null;

        // Iterate from the bottom (oldest card) to the top (newest card)
        for (Card card : column) {
            if (!card.isFaceUp()) {
                sequence.clear(); // Sequence breaks at a face-down card
                continue;
            }

            if (previousCard == null || 
                (previousCard.getRank().ordinal() - 1 == card.getRank().ordinal() && 
                 previousCard.getSuit().getColor() != card.getSuit().getColor())) {
                sequence.add(card); // Add to sequence if it follows descending order and alternating color
            } else {
                sequence.clear(); // Sequence breaks if order or color rule is violated
            }

            previousCard = card;
        }

        return sequence;
    }

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

         // Updated tableau-to-tableau move logic to handle sequences
            if (!moved) {
                boolean sequenceMoved = false;

                for (int i = 0; i < tableau.size(); i++) {
                    ArrayDeque<Card> sourceColumn = tableau.get(i);

                    if (!sourceColumn.isEmpty()) {
                        // Get the movable sequence from the source column
                        List<Card> movableSequence = getMovableSequence(sourceColumn);

                        if (!movableSequence.isEmpty() && !sequenceMoved) {
                            // Iterate over tableau columns to find a valid move
                            for (int j = 0; j < tableau.size(); j++) {
                                if (i != j) {
                                    ArrayDeque<Card> targetColumn = tableau.get(j);

                                    // Get the top card of the sequence
                                    Card topCard = movableSequence.get(0);

                                    // Condition to prevent moving a King if it is already at the first index of the column
                                    if (topCard.getRank() == Rank.KING && sourceColumn.peekFirst() == topCard) {
                                        continue; // Skip moving the King if it's already at the first index of the column
                                    }

                                    // Check if the sequence can be moved to an empty column (valid for King)
                                    if (targetColumn.isEmpty() && topCard.getRank() == Rank.KING) {
                                        // Move the whole sequence to the empty column
                                        for (Card card : movableSequence) {
                                            sourceColumn.remove(card);
                                            targetColumn.addLast(card);
                                        }

                                        // Flip the next card in the source column, if any
                                        if (!sourceColumn.isEmpty()) {
                                            sourceColumn.peekLast().setFaceUp(true);
                                        }

                                        moved = true;
                                        updated = true;
                                        moves++;
                                        sequenceMoved = true;  // Mark sequence as moved
                                        System.out.println("Moved sequence " + movableSequence + " to Column " + (j + 1));
                                        break;
                                    }
                                    // Check if the sequence can be moved to a non-empty column (alternating colors, descending order)
                                    else if (!targetColumn.isEmpty()) {
                                        Card targetTopCard = targetColumn.peekLast();

                                        // Ensure the sequence can only move if it follows the rules: descending, alternating colors
                                        if (targetTopCard.getRank().ordinal() == topCard.getRank().ordinal() + 1 &&
                                            targetTopCard.getSuit().getColor() != topCard.getSuit().getColor()) {
                                            // Move the whole sequence to the target column
                                            for (Card card : movableSequence) {
                                                sourceColumn.remove(card);
                                                targetColumn.addLast(card);
                                            }

                                            // Flip the next card in the source column, if any
                                            if (!sourceColumn.isEmpty()) {
                                                sourceColumn.peekLast().setFaceUp(true);
                                            }

                                            moved = true;
                                            updated = true;
                                            moves++;
                                            sequenceMoved = true;  // Mark sequence as moved
                                            System.out.println("Moved sequence " + movableSequence + " to Column " + (j + 1));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (moved) {
                        break;
                    }
                }
            }


            // 3. Try moving cards from talon to tableau or foundation
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
            if (!moved && talon.isEmpty() && !waste.isEmpty() && passesThroughTalon < 3) {
                System.out.println("Redealing cards from waste to talon...");
                redealTalon();
                updated = true;
            }

            // Check if the game is won
            if (CardMovementHandler.validateGameWon(foundation)) {
                gameOver = true;
                System.out.println("Congratulations! You won!");
                printGameStatus();
                updateGameState();
                return;
            }

            // Check if there are no valid moves left (auto exit condition)
            if (!updated || !hasValidMoves()) {
                System.out.println("No valid moves left. Game over!");
                gameOver = true;
            } else {
                // Update the game state
                printGameStatus();
                updateGameState();
            }
        }
    }
    
    // Check for valid moves across tableau, foundation, and talon
    private boolean hasValidMoves() {
        for (ArrayDeque<Card> column : tableau) {
            if (!column.isEmpty() && column.peekLast().isFaceUp()) {
                return true; // There are cards that can be moved
            }
        }


        // Check for valid talon-to-foundation or talon-to-tableau moves
        if (!talon.isEmpty()) {
            Card topCard = talon.peek();
            if (addToFoundation(topCard)) {
                return true;
            }

            for (ArrayDeque<Card> column : tableau) {
                if (column.isEmpty() && topCard.getRank() == Rank.KING) {
                    return true;
                } else {
                    Card topColumnCard = column.peekLast();
                    if (topColumnCard != null &&
                            topColumnCard.getRank().ordinal() == topCard.getRank().ordinal() + 1 &&
                            topColumnCard.getSuit().getColor() != topCard.getSuit().getColor()) {
                        return true;
                    }
                }
            }
        }

        return false; // No valid moves left
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