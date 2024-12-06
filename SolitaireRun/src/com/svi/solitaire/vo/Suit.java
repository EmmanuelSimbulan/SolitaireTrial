package com.svi.solitaire.vo;

/**
 * The {@code Suit} enum represents the four suits in a standard deck of playing cards.
 * Each suit has a symbol and a color associated with it.
 * The suits are HEARTS, DIAMONDS, CLUBS, and SPADES.
 */
public enum Suit {
    /**
     * Hearts suit, represented by the symbol "♥" and color "RED".
     */
    HEARTS("♥", "RED"), 
    
    /**
     * Diamonds suit, represented by the symbol "♦" and color "RED".
     */
    DIAMONDS("♦", "RED"),
    
    /**
     * Clubs suit, represented by the symbol "♣" and color "BLACK".
     */
    CLUBS("♣", "BLACK"), 
    
    /**
     * Spades suit, represented by the symbol "♠" and color "BLACK".
     */
    SPADES("♠", "BLACK");

    private final String symbol;
    private final String color; // Added color attribute

    /**
     * Constructs a {@code Suit} with the specified symbol and color.
     *
     * @param symbol the symbol representing the suit
     * @param color the color of the suit
     */
    Suit(String symbol, String color) {
        this.symbol = symbol;
        this.color = color; // Initialize color
    }

    /**
     * Returns the symbol of the suit.
     *
     * @return the symbol of the suit
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the color of the suit.
     *
     * @return the color of the suit
     */
    public String getColor() {
        return color; // Getter for color
    }
}