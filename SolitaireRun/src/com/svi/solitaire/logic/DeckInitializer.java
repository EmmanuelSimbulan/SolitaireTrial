package com.svi.solitaire.logic;

import java.util.ArrayList;
import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.Rank;
import com.svi.solitaire.vo.Suit;

/**
 * The {@code DeckInitializer} class is responsible for creating and initializing
 * a complete deck of cards. Each card in the deck is represented as an instance 
 * of the {@code Card} class and consists of a rank and a suit.
 * <p>
 * This class leverages the {@code Rank} and {@code Suit} enumerations to generate 
 * all possible card combinations, ensuring a complete standard 52-card deck is created.
 * </p>
 * <p>
 * This initialized deck is primarily used in other classes such as {@code GameState}
 * for managing and manipulating the game state in Klondike Solitaire.
 * </p>
 * 
 * @see com.svi.solitaire.vo.Card
 * @see com.svi.solitaire.vo.Rank
 * @see com.svi.solitaire.vo.Suit
 * @author Emmanuel Simbulan
 */
public class DeckInitializer {
    /**
     * The list representing the complete deck of cards.
     */
    private ArrayList<Card> deck;

    /**
     * Constructs a new {@code DeckInitializer} object and initializes
     * the deck with 52 standard playing cards. Each card is a combination
     * of a rank and a suit from the {@code Rank} and {@code Suit} enums.
     */
    public DeckInitializer() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) { // Iterate through all suit values
            for (Rank rank : Rank.values()) { // Iterate through all rank values
                deck.add(new Card(rank, suit)); // Create a new card and add it to the deck
            }
        }
    }

    /**
     * Retrieves the initialized deck of cards.
     * 
     * @return an {@code ArrayList} containing all 52 cards in the standard deck.
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }
}
