package com.svi.solitaire.logic;

import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.Rank;
import com.svi.solitaire.vo.Suit;

import java.util.ArrayDeque;
import java.util.List;

public class CardMovementHandler {

    // Ensure the shuffled deck is not null or empty
    public static void validateDeck(ArrayDeque<Card> shuffledDeck) {
        if (shuffledDeck == null || shuffledDeck.isEmpty()) {
            throw new IllegalArgumentException("Shuffled deck cannot be null or empty.");
        }
        if (shuffledDeck.size() < 52) {
            throw new IllegalStateException("Shuffled deck contains less than 52 cards.");
        }
    }

    // Validate the card distribution process
    public static void validateCardDistribution(Card card) {
        if (card == null) {
            throw new IllegalStateException("Unexpected null card encountered while distributing.");
        }
    }

    // Validate adding a card to the foundation
    public static boolean validateAddToFoundation(Card card, List<ArrayDeque<Card>> foundation) {
        for (ArrayDeque<Card> pile : foundation) {
            if (pile.isEmpty()) {
                if (card.getRank() == Rank.ACE) {
                    return true;
                }
            } else {
                Card topCard = pile.peek();
                int topRankValue = Card.getRankValue(topCard.getRank());
                int cardRankValue = Card.getRankValue(card.getRank());

                if (topCard.getSuit().equals(card.getSuit()) &&
                    cardRankValue == topRankValue + 1) {
                    return true;
                }
            }
        }
        return false; // Card cannot be added to any foundation pile
    }

    // Validate if the game is won (all foundation piles are complete)
    public static boolean validateGameWon(List<ArrayDeque<Card>> foundation) {
        for (ArrayDeque<Card> pile : foundation) {
            if (pile.size() != 13) {
                return false;
            }
        }
        return true;
    }

    // Validate if a tableau move can be made (card from tableau to foundation or tableau)
    public static boolean validateTableauMove(Card topCard, List<ArrayDeque<Card>> tableau) {
        for (ArrayDeque<Card> column : tableau) {
            if (!column.isEmpty()) {
                Card tableauCard = column.peek(); // Only the top card can be moved
                // A tableau move is valid if the top card is one rank lower than the tableau card
                if (tableauCard.getRank().ordinal() == topCard.getRank().ordinal() + 1) {
                    // Ensure alternating colors between tableau cards
                    if (isAlternatingColor(tableauCard, topCard)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Validate if a talon card can be placed in the tableau or foundation
    public static boolean validateTalonMove(Card topCard, List<ArrayDeque<Card>> tableau, List<ArrayDeque<Card>> foundation) {
        // If the talon card can be added to the foundation, return true
        if (validateAddToFoundation(topCard, foundation)) {
            return true;
        }

        // Otherwise, check if the talon card can be placed in the tableau
        return validateTableauMove(topCard, tableau);
    }

    // Check if two cards have alternating colors (red/black)
    private static boolean isAlternatingColor(Card card1, Card card2) {
        boolean card1Red = card1.getSuit() == Suit.HEARTS || card1.getSuit() == Suit.DIAMONDS;
        boolean card2Red = card2.getSuit() == Suit.HEARTS || card2.getSuit() == Suit.DIAMONDS;

        return card1Red != card2Red; // One should be red, the other black
    }

    // Validate movement of a card to an empty tableau column (only a King can go in an empty column)
    public static boolean validateEmptyTableauColumnMove(Card card) {
        return card.getRank() == Rank.KING; // Only Kings can be moved to empty tableau columns
    }

    // Validate that only the top card of the tableau column can be moved
    public static boolean validateTopCardMove(ArrayDeque<Card> column) {
        return column.size() > 0 && column.peek().isFaceUp(); // The top card must be face-up to move
    }

    // Validate if a move from tableau to tableau is valid based on the descending order and alternating colors
    public static boolean validateTableauToTableauMove(Card cardToMove, ArrayDeque<Card> destinationColumn) {
        // If destination column is empty, only a King can be placed
        if (destinationColumn.isEmpty()) {
            return cardToMove.getRank() == Rank.KING;
        }
        
        // Validate the top card in the destination tableau column
        Card topCardInDestination = destinationColumn.peek();

        // Check if the move is in descending order and alternating colors
        return (topCardInDestination.getRank().ordinal() == cardToMove.getRank().ordinal() + 1) && 
               isAlternatingColor(topCardInDestination, cardToMove);
    }
}
