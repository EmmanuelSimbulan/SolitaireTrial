package com.svi.solitaire.logic;

import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.Rank;

import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles card movements and validations for the Klondike Solitaire game.
 */
public class CardMovementHandler {

    /**
     * Validates the shuffled deck.
     * Ensures the deck is not null, empty, or contains fewer than 52 cards.
     *
     * @param shuffledDeck the shuffled deck to validate.
     * @throws IllegalArgumentException if the deck is null or empty.
     * @throws IllegalStateException if the deck contains fewer than 52 cards.
     */
    public static void validateDeck(ArrayDeque<Card> shuffledDeck) {
        if (shuffledDeck == null || shuffledDeck.isEmpty()) {
            throw new IllegalArgumentException("Shuffled deck cannot be null or empty.");
        }
        if (shuffledDeck.size() < 52) {
            throw new IllegalStateException("Shuffled deck contains less than 52 cards.");
        }
    }

    /**
     * Validates a card during distribution to ensure it is not null.
     *
     * @param card the card to validate.
     * @throws IllegalStateException if the card is null.
     */
    public static void validateCardDistribution(Card card) {
        if (card == null) {
            throw new IllegalStateException("Unexpected null card encountered while distributing.");
        }
    }

    /**
     * Adds a card to the foundation if it follows the rules.
     * Aces can be placed as the first card, and subsequent cards must follow ascending order.
     *
     * @param card       the card to be added to the foundation.
     * @param foundation the list of foundation piles.
     * @return {@code true} if the card is successfully added to the foundation; otherwise, {@code false}.
     */
    public static boolean addToFoundation(Card card, List<ArrayDeque<Card>> foundation) {
        int foundationIndex = card.getSuit().ordinal();

        if (card.getRank() == Rank.ACE) {
            foundation.get(foundationIndex).push(card);
            return true;
        } else {
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

    /**
     * Checks if the game is won by verifying all foundation piles are complete.
     *
     * @param foundation the list of foundation piles.
     * @return {@code true} if all foundation piles contain 13 cards; otherwise, {@code false}.
     */
    public static boolean validateGameWon(List<ArrayDeque<Card>> foundation) {
        for (ArrayDeque<Card> pile : foundation) {
            if (pile.size() != 13) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if a card from the talon can be moved to the tableau or foundation.
     *
     * @param topCard    the top card of the talon.
     * @param tableau    the list of tableau columns.
     * @param foundation the list of foundation piles.
     * @return {@code true} if the card can be moved; otherwise, {@code false}.
     */
    public static boolean validateTalonMove(Card topCard, List<ArrayDeque<Card>> tableau, List<ArrayDeque<Card>> foundation) {
        if (addToFoundation(topCard, foundation)) {
            return true;
        }
        return validateTableauMove(topCard, tableau);
    }

    /**
     * Validates if a card can be placed in an empty tableau column.
     * Only Kings can be placed in empty columns.
     *
     * @param card the card to validate.
     * @return {@code true} if the card is a King; otherwise, {@code false}.
     */
    public static boolean validateEmptyTableauColumnMove(Card card) {
        return card.getRank() == Rank.KING;
    }

    /**
     * Validates if a card can be moved to a tableau column based on game rules.
     *
     * @param cardToMove the card to be moved.
     * @param tableau    the list of tableau columns.
     * @return {@code true} if the card can be moved; otherwise, {@code false}.
     */
    public static boolean validateTableauMove(Card cardToMove, List<ArrayDeque<Card>> tableau) {
        for (ArrayDeque<Card> column : tableau) {
            if (column.isEmpty()) {
                if (validateEmptyTableauColumnMove(cardToMove)) {
                    logCardMovement(cardToMove, "Column", tableau.indexOf(column) + 1);
                    return true;
                }
            } else {
                Card topColumnCard = column.peek();
                if (topColumnCard.getRank().ordinal() == cardToMove.getRank().ordinal() + 1 &&
                    topColumnCard.getSuit().getColor() != cardToMove.getSuit().getColor()) {
                    logCardMovement(cardToMove, "Column", tableau.indexOf(column) + 1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves a movable sequence of cards from a tableau column.
     * A sequence follows descending order and alternating colors.
     *
     * @param column the tableau column to analyze.
     * @return a list of cards representing the movable sequence.
     */
    public static List<Card> getMovableSequence(ArrayDeque<Card> column) {
        List<Card> sequence = new ArrayList<>();
        Card previousCard = null;

        for (Card card : column) {
            if (!card.isFaceUp()) {
                sequence.clear();
                continue;
            }

            if (previousCard == null || 
                (previousCard.getRank().ordinal() - 1 == card.getRank().ordinal() && 
                 previousCard.getSuit().getColor() != card.getSuit().getColor())) {
                sequence.add(card);
            } else {
                sequence.clear();
            }

            previousCard = card;
        }

        return sequence;
    }

    /**
     * Logs the movement of a card.
     *
     * @param card            the card being moved.
     * @param destinationType the type of destination (e.g., "Column").
     * @param destinationIndex the index of the destination.
     */
    public static void logCardMovement(Card card, String destinationType, int destinationIndex) {
        System.out.println("Card (" + card.getRank() + card.getSuit().getSymbol() + 
                           ") is moved to " + destinationType + " " + destinationIndex);
    }
}
