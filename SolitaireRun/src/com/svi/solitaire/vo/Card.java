package com.svi.solitaire.vo;

/**
 * Represents a playing card with a rank, suit, and face-up state.
 * This class is used to model the cards in a Klondike Solitaire game.
 */
public class Card {

    /** The rank of the card, represented using the {@link Rank} enum. */
    private final Rank rank;

    /** The suit of the card, represented using the {@link Suit} enum. */
    private final Suit suit;

    /** The face-up state of the card. {@code true} if the card is face-up, {@code false} if face-down. */
    private boolean isFaceUp;

    /**
     * Constructs a new {@code Card} with the specified rank and suit.
     * The card is initially face-down.
     *
     * @param rank the rank of the card (e.g., ACE, TWO, KING, etc.)
     * @param suit the suit of the card (e.g., HEARTS, SPADES, DIAMONDS, CLUBS)
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = false;  // Default to face-down
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank of the card
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the suit of the card.
     *
     * @return the suit of the card
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Returns whether the card is face-up.
     *
     * @return {@code true} if the card is face-up, {@code false} if face-down
     */
    public boolean isFaceUp() {
        return isFaceUp;
    }

    /**
     * Sets the face-up state of the card.
     *
     * @param isFaceUp {@code true} to make the card face-up, {@code false} to make it face-down
     */
    public void setFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
    }

    /**
     * Returns a string representation of the card, combining its rank and suit symbols.
     * This method is used to print the card in a human-readable format.
     *
     * @return a string representation of the card (e.g., "AS" for Ace of Spades)
     */
    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }

    /**
     * Returns the numerical value of the card's rank.
     * The rank value is used for sorting or comparing cards in the game.
     *
     * @param rank the rank of the card (e.g., ACE, TWO, KING)
     * @return the numerical value of the card's rank
     */
    public static int getRankValue(Rank rank) {
        return rank.getValue();
    }

    /**
     * Determines if the card is red (i.e., belongs to the Hearts or Diamonds suits).
     *
     * @return {@code true} if the card is red, {@code false} if it is not
     */
    public boolean isRed() {
        return suit == Suit.HEARTS || suit == Suit.DIAMONDS;
    }

    /**
     * Determines if the card is black (i.e., belongs to the Clubs or Spades suits).
     *
     * @return {@code true} if the card is black, {@code false} if it is not
     */
    public boolean isBlack() {
        return suit == Suit.CLUBS || suit == Suit.SPADES;
    }

    /**
     * Checks if this card can be placed on top of another card in the tableau.
     * The card can be placed on top if it is one rank lower and has an opposite color from the other card.
     *
     * @param card the card to check if this card can be placed on
     * @return {@code true} if this card can be placed on the other card, {@code false} otherwise
     */
    public boolean canBePlacedOn(Card card) {
        // Cards can only be placed on top if they are one rank lower and of opposite color
        return this.getRank().ordinal() == card.getRank().ordinal() - 1 &&
               this.getSuit().getColor() != card.getSuit().getColor();
    }
}
