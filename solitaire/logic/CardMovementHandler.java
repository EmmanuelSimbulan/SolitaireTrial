 package com.svi.solitaire.logic;

import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.Rank;
import com.svi.solitaire.vo.Suit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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

    // Validate if the game is won (all foundation piles are complete)
    public static boolean validateGameWon(List<ArrayDeque<Card>> foundation) {
        for (ArrayDeque<Card> pile : foundation) {
            if (pile.size() != 13) {
                return false;
            }
        }
        return true;
    }
        

    // Method to log card movements
    public static void logCardMovement(Card card, String destinationType, int destinationIndex) {
        System.out.println("Card (" + card.getRank() + card.getSuit().getSymbol() + 
                           ") is moved to " + destinationType + " " + destinationIndex);
    }

}
