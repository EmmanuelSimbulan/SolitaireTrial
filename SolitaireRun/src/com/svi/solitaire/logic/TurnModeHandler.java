package com.svi.solitaire.logic;

/**
 * Handles the configuration and management of turn modes for Klondike Solitaire.
 * The two supported modes are:
 * <ul>
 *     <li>Turn 1: One card is dealt at a time from the talon.</li>
 *     <li>Turn 3: Three cards are dealt at a time from the talon.</li>
 * </ul>
 * This class ensures that the turn mode is set explicitly and cannot be changed
 * once configured, promoting consistent game behavior.
 */
public class TurnModeHandler {

    /**
     * Enum representing the two possible turn modes.
     * Provides type-safety and encapsulates the mode as an integer value.
     */
    public enum TurnMode {
        TURN_1(1), // Turn 1 mode: deal 1 card at a time
        TURN_3(3); // Turn 3 mode: deal 3 cards at a time

        private final int mode;

        /**
         * Constructor to associate an integer value with the turn mode.
         *
         * @param mode the integer value representing the turn mode.
         */
        TurnMode(int mode) {
            this.mode = mode;
        }

        /**
         * Gets the integer value associated with the turn mode.
         *
         * @return the integer value of the turn mode (1 or 3).
         */
        public int getMode() {
            return this.mode;
        }
    }

    private TurnMode turnMode = null; // Initially null to ensure explicit configuration

    /**
     * Constructor for TurnModeHandler.
     * By default, no turn mode is set. The mode must be explicitly configured.
     */
    public TurnModeHandler() {
        // Intentionally empty to enforce explicit mode setting
    }

    /**
     * Sets the turn mode for the game. 
     * The valid modes are 1 (Turn 1) or 3 (Turn 3). Once the mode is set, it cannot be changed.
     *
     * @param mode the integer value representing the turn mode (1 or 3).
     * @throws IllegalArgumentException if the provided mode is not valid (must be 1 or 3).
     */
    public void setTurnMode(int mode) {
        if (turnMode != null) {
            System.out.println("Turn mode is already set to: " + (turnMode == TurnMode.TURN_1 ? "Turn 1" : "Turn 3"));
            return; // Prevent changing the mode after it is set
        }

        if (mode == TurnMode.TURN_1.getMode()) {
            turnMode = TurnMode.TURN_1;
            System.out.println("\nTurn mode set to: Turn 1");
        } else if (mode == TurnMode.TURN_3.getMode()) {
            turnMode = TurnMode.TURN_3;
            System.out.println("\nTurn mode set to: Turn 3");
        } else {
            throw new IllegalArgumentException("Invalid turn mode. Must be 1 or 3.");
        }
    }

    /**
     * Gets the currently set turn mode.
     *
     * @return the {@link TurnMode} representing the current turn mode.
     * @throws IllegalStateException if the turn mode has not been set yet.
     */
    public TurnMode getTurnMode() {
        if (turnMode == null) {
            throw new IllegalStateException("Turn mode is not set yet. Please set it first.");
        }
        return turnMode;
    }

    /**
     * Gets the turn mode as an integer value for compatibility with other components.
     *
     * @return the integer value representing the turn mode (1 or 3).
     * @throws IllegalStateException if the turn mode has not been set yet.
     */
    public int getTurnModeAsInt() {
        if (turnMode == null) {
            throw new IllegalStateException("Turn mode is not set yet. Please set it first.");
        }
        return turnMode.getMode();
    }
}
