package com.experiments.snakeAndLadder.model;

import com.experiments.snakeAndLadder.exceptions.GameException;
import com.experiments.snakeAndLadder.model.board.Board;
import com.experiments.snakeAndLadder.model.dice.Dice;
import lombok.Getter;

import java.util.List;

@Getter
public class Game {
    private final String id;
    private final Board board;
    private final List<Player> players;
    private final Dice dice;
    private GameState state;

    public Game(String id, Board board, List<Player> players, Dice dice) {
        this.id = id;
        this.board = board;
        this.players = List.copyOf(players);
        this.dice = dice;
        this.state = new GameState();
    }

    public boolean makeMove(int playerId) {
        Player player;
        synchronized (this) {
            // Only synchronize the player validation and state check
            player = findPlayer(playerId);
            if (player == null || !state.isPlayerTurn(playerId) || state.isEnded()) {
                return false;
            }
        }

        // Dice roll and position calculation don't need synchronization
        int diceValue = dice.roll();
        Position newPosition = board.getNextPosition(player.getPosition(), diceValue);

        synchronized (this) {
            // Synchronize only the state update
            try {
                player.setPosition(newPosition);

                if (board.isWinningPosition(newPosition)) {
                    state = state.end();
                } else {
                    state = state.nextTurn(players.size());
                }
                return true;
            } catch (GameException e) {
                return false;
            }
        }
    }

    private Player findPlayer(int playerId) {
        return players.stream()
                .filter(p -> p.getId() == playerId)
                .findFirst()
                .orElseThrow(() -> new GameException.PlayerNotFound(playerId));
    }

}