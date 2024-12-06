package com.svi.solitaire.logic;

public class TurnModeHandler {
    // Enum for the turn mode to ensure type-safety
    public enum TurnMode {
        TURN_1(1),
        TURN_3(3);

        private final int mode;

        TurnMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }
    }

    private TurnMode turnMode = null; // Initially null to check if it is set

    public TurnModeHandler() {
        // No default turn mode, must be set explicitly from the GameMenu
    }

    /**
     * Sets the turn mode (1 for Turn 1, 3 for Turn 3).
     * Once set, it cannot be changed.
     */
    public void setTurnMode(int mode) {
        if (turnMode != null) {
            System.out.println("Turn mode is already set to: " + (turnMode == TurnMode.TURN_1 ? "Turn 1" : "Turn 3"));
            return; // Prevent changing after it's set
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
     * Gets the current turn mode.
     * @return the current turn mode (1 or 3).
     */
    public TurnMode getTurnMode() {
        if (turnMode == null) {
            throw new IllegalStateException("Turn mode is not set yet. Please set it first.");
        }
        return turnMode;
    }

    /**
     * Returns the turn mode as an int for compatibility with other components.
     * @return the current turn mode (1 or 3).
     */
    public int getTurnModeAsInt() {
        return turnMode.getMode();
    }
}
