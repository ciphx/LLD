package com.experiments.snakeAndLadder.model;

public record GameState(int currentPlayerIndex, boolean isEnded) {
    public GameState() {
        this(0, false);
    }

    public GameState nextTurn(int totalPlayers) {
        return new GameState((currentPlayerIndex + 1) % totalPlayers, isEnded);
    }

    public GameState end() {
        return new GameState(currentPlayerIndex, true);
    }

    public boolean isPlayerTurn(int playerId) {
        return currentPlayerIndex == playerId;
    }
} 