package com.experiments.snakeAndLadder.exceptions;

public sealed class GameException extends RuntimeException {
    private GameException(String message) { super(message); }

    public static final class PlayerNotFound extends GameException {
        public PlayerNotFound(int playerId) {
            super("Player not found: " + playerId);
        }
    }

    public static final class InvalidBoardSize extends GameException {
        public InvalidBoardSize(int size){
            super("Invalid board size " + size);
        }
    }

    public class GameNotFoundException extends Throwable {
        public GameNotFoundException(String s) {
        }
    }
} 