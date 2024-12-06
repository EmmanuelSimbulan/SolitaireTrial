package com.svi.solitaire.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.svi.solitaire.utilities.CardRenderer;
import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.GameState;

/**
 * Handles various shuffle algorithms and deck operations for the Klondike Solitaire game.
 * This class provides methods to shuffle, unshuffle, and handle the shuffled deck during initialization.
 */
public class ShuffleManager {

    /**
     * Sorts the deck in ascending order by suit and rank.
     *
     * @param deck the original deck of cards to be sorted.
     * @return a new {@link ArrayDeque} containing the sorted cards.
     */
    public static ArrayDeque<Card> unShuffle(ArrayDeque<Card> deck) {
        ArrayList<Card> deckList = new ArrayList<>(deck);

        // Sort cards fully by suit and rank in ascending order
        Collections.sort(deckList, (firstCard, secondCard) -> {
            if (firstCard.getSuit() == secondCard.getSuit()) {
                return firstCard.getRank().ordinal() - secondCard.getRank().ordinal();
            }
            return firstCard.getSuit().compareTo(secondCard.getSuit());
        });

        return new ArrayDeque<>(deckList);
    }

    /**
     * Performs an In-Faro shuffle on the deck. This shuffle interleaves the second half of the deck with the first half.
     *
     * @param deck the original deck to be shuffled.
     * @return a new {@link ArrayDeque} representing the shuffled deck.
     */
    public static ArrayDeque<Card> inFaroShuffle(ArrayDeque<Card> deck) {
        ArrayList<Card> deckList = new ArrayList<>(deck);
        int middleIndex = deckList.size() / 2;
        List<Card> firstHalf = deckList.subList(0, middleIndex);
        List<Card> secondHalf = deckList.subList(middleIndex, deckList.size());

        ArrayDeque<Card> shuffledDeck = new ArrayDeque<>();
        for (int index = 0; index < middleIndex; index++) {
            shuffledDeck.add(secondHalf.get(index)); // Add from the second half
            shuffledDeck.add(firstHalf.get(index)); // Add from the first half
        }
        return shuffledDeck;
    }

    /**
     * Performs an Out-Faro shuffle on the deck. This shuffle alternates cards from the first half and the second half.
     *
     * @param deck the original deck to be shuffled.
     * @return a new {@link ArrayDeque} representing the shuffled deck.
     */
    public static ArrayDeque<Card> outFaroShuffle(ArrayDeque<Card> deck) {
        ArrayList<Card> deckList = new ArrayList<>(deck);
        ArrayList<Card> shuffledDeck = new ArrayList<>();

        int middleIndex = deckList.size() / 2;
        List<Card> firstHalf = deckList.subList(0, middleIndex);
        List<Card> secondHalf = deckList.subList(middleIndex, deckList.size());

        for (int firstHalfIndex = 0; firstHalfIndex < firstHalf.size(); firstHalfIndex++) {
            shuffledDeck.add(firstHalf.get(firstHalfIndex));
            if (firstHalfIndex < secondHalf.size()) {
                shuffledDeck.add(secondHalf.get(firstHalfIndex));
            }
        }

        return new ArrayDeque<>(shuffledDeck);
    }

    /**
     * Performs a standard random shuffle on the deck.
     *
     * @param deck the original deck to be shuffled.
     * @return a new {@link ArrayDeque} containing the shuffled cards.
     */
    public static ArrayDeque<Card> normalShuffle(ArrayDeque<Card> deck) {
        ArrayList<Card> deckList = new ArrayList<>(deck);
        Collections.shuffle(deckList); // Uses the default shuffle logic of Collections.shuffle
        return new ArrayDeque<>(deckList);
    }

    /**
     * Performs a hard shuffle on the deck, ensuring a completely random order.
     *
     * @param deck the original deck to be shuffled.
     * @return a new {@link ArrayDeque} containing the shuffled cards.
     */
    public static ArrayDeque<Card> hardShuffle(ArrayDeque<Card> deck) {
        ArrayList<Card> deckList = new ArrayList<>(deck);
        Collections.shuffle(deckList);
        return new ArrayDeque<>(deckList);
    }

    /**
     * Distributes the shuffled deck into the game state for gameplay initialization.
     *
     * @param shuffledDeck the shuffled deck of cards.
     * @param gameState the current state of the game where the cards will be distributed.
     */
    public static void handleShuffledDeck(ArrayDeque<Card> shuffledDeck, GameState gameState) {
        // Only shuffle and distribute, no rendering
        gameState.distributeCards(shuffledDeck);
    }
}
