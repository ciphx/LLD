package com.experiments.snakeAndLadder.factory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.experiments.snakeAndLadder.exceptions.GameException;
import com.experiments.snakeAndLadder.model.Game;
import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.model.board.Board;
import com.experiments.snakeAndLadder.model.dice.StandardDice;
import com.experiments.snakeAndLadder.model.Position;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultGameFactory implements GameFactory {
    private static final int DEFAULT_DICE_SIDES = 6;

    @Override
    public Game createGame(int boardSize, Map<Integer, Integer> snakes,
                           Map<Integer, Integer> ladders, List<Integer> playerIds) {
        validateBoardSize(boardSize);
        
        return new Game(
            generateGameId(),
            createBoard(boardSize, snakes, ladders),
            createPlayers(playerIds),
            new StandardDice(DEFAULT_DICE_SIDES)
        );
    }

    private String generateGameId() {
        return UUID.randomUUID().toString();
    }

    private Board createBoard(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        return new Board(
            size, snakes, ladders
        );
    }

    private List<Player> createPlayers(List<Integer> playerIds) {
        return playerIds.stream()
            .map(id -> {
                Player player = new Player(id);
                player.setPosition(new Position(0));
                return player;
            })
            .toList();
    }

    private void validateBoardSize(int boardSize) {
        if (boardSize <= 0) {
            throw new GameException.InvalidBoardSize(boardSize);
        }
    }
} 