package com.svi.solitaire.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.svi.solitaire.utilities.CardRenderer;
import com.svi.solitaire.vo.Card;
import com.svi.solitaire.vo.GameState;

public class ShuffleManager {
	

	// Implements unshuffle
	public static ArrayDeque<Card> unShuffle(ArrayDeque<Card> deck) {
		ArrayList<Card> deckList = new ArrayList<>(deck);

		// Sort cards fully by suit and rank in ascending order
		Collections.sort(deckList, (card1, card2) -> {
			if (card1.getSuit() == card2.getSuit()) {
				return card1.getRank().ordinal() - card2.getRank().ordinal();
			}
			return card1.getSuit().compareTo(card2.getSuit());
		});

		return new ArrayDeque<>(deckList);
	}

	// Implements a In-Faro shuffle.
	public static ArrayDeque<Card> inFaroShuffle(ArrayDeque<Card> deck) {
		ArrayList<Card> deckList = new ArrayList<>(deck);
		int mid = deckList.size() / 2;
		List<Card> firstHalf = deckList.subList(0, mid);
		List<Card> secondHalf = deckList.subList(mid, deckList.size());

		ArrayDeque<Card> shuffledDeck = new ArrayDeque<>();
		for (int i = 0; i < mid; i++) {
			shuffledDeck.add(secondHalf.get(i)); // Add from the second half
			shuffledDeck.add(firstHalf.get(i)); // Add from the first half
		}
		return shuffledDeck;
	}

	// Implements a Out-Faro shuffle.
	public static ArrayDeque<Card> outFaroShuffle(ArrayDeque<Card> deck) {
		ArrayList<Card> deckList = new ArrayList<>(deck);
		ArrayList<Card> shuffledDeck = new ArrayList<>();

		int mid = deckList.size() / 2;
		List<Card> firstHalf = deckList.subList(0, mid);
		List<Card> secondHalf = deckList.subList(mid, deckList.size());

		for (int i = 0; i < firstHalf.size(); i++) {
			shuffledDeck.add(firstHalf.get(i));
			if (i < secondHalf.size()) {
				shuffledDeck.add(secondHalf.get(i));
			}
		}

		return new ArrayDeque<>(shuffledDeck);
	}

	// Hard Shuffle: Completely random shuffle for maximum difficulty.
	public static ArrayDeque<Card> hardShuffle(ArrayDeque<Card> deck) {
		ArrayList<Card> deckList = new ArrayList<>(deck);
		Collections.shuffle(deckList);
		return new ArrayDeque<>(deckList);
	}

	// Handle shuffled deck display and initialization
	public static void handleShuffledDeck(ArrayDeque<Card> shuffledDeck, GameState gameState) {
		// Only shuffle and distribute, no rendering
		gameState.distributeCards(shuffledDeck);
	}
}
