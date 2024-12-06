package com.svi.solitaire.vo;

/**
 * The {@code Rank} enum represents the rank of a card in a deck of cards.
 * Each rank has a symbol and a numeric value associated with it.
 */
public enum Rank {
    ACE("A", 1), 
    TWO("2", 2), 
    THREE("3", 3), 
    FOUR("4", 4), 
    FIVE("5", 5), 
    SIX("6", 6),
    SEVEN("7", 7), 
    EIGHT("8", 8), 
    NINE("9", 9), 
    TEN("10", 10), 
    JACK("J", 11), 
    QUEEN("Q", 12), 
    KING("K", 13);

    /** The symbol of the rank. */
    private final String symbol;

    /** The numeric value of the rank. */
    private final int value;

    /**
     * Constructs a {@code Rank} with the specified symbol and value.
     *
     * @param symbol the symbol of the rank
     * @param value the numeric value of the rank
     */
    Rank(String symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }

    /**
     * Returns the symbol of the rank.
     *
     * @return the symbol of the rank
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the numeric value of the rank.
     *
     * @return the numeric value of the rank
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if a card rank is one less than another rank.
     *
     * @param rank1 the first rank to compare
     * @param rank2 the second rank to compare
     * @return {@code true} if the value of {@code rank1} is one less than the value of {@code rank2}, otherwise {@code false}
     */
    public static boolean isOneRankLower(Rank rank1, Rank rank2) {
        return rank1.getValue() == rank2.getValue() - 1;
    }
}