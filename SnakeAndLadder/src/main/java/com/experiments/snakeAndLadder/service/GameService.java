package com.experiments.snakeAndLadder.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.experiments.snakeAndLadder.factory.GameFactory;
import com.experiments.snakeAndLadder.model.Game;
import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.repository.PlayerHistoryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameService implements GameApplication {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final GameFactory gameFactory;
    private final PlayerHistoryRepository playerHistory;
    private final ConcurrentMap<Integer, ReentrantLock> playerLocks = new ConcurrentHashMap<>();
    private final Map<String, Integer> diceHolders = new ConcurrentHashMap<>();

    @Override
    public String createGame(int boardSize, Map<Integer, Integer> snakes,
                           Map<Integer, Integer> ladders, List<Integer> playerIds) {
        validateGameCreationParameters(boardSize, playerIds);

        acquirePlayerLocks(playerIds);
        try {
            validatePlayersAvailability(playerIds);
            Game game = gameFactory.createGame(boardSize, snakes, ladders, playerIds);
            activeGames.put(game.getId(), game);
            playerHistory.recordGame(playerIds, game.getId());
            return game.getId();
        } finally {
            releasePlayerLocks();
        }
    }

    @Override
    public boolean holdDice(String gameId, int playerId) {
        if (!activeGames.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }

        Game game = activeGames.get(gameId);
        if (!game.getState().isPlayerTurn(playerId) || game.getState().isEnded()) {
            return false;
        }

        Integer currentHolder = diceHolders.get(gameId);
        if (currentHolder != null && currentHolder != playerId) {
            return false;
        }

        diceHolders.put(gameId, playerId);
        return true;
    }

    @Override
    public boolean rollDiceAndMove(String gameId, int playerId) {
        if (!activeGames.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }

        Integer diceHolder = diceHolders.get(gameId);
        if (diceHolder == null || diceHolder != playerId) {
            return false;
        }

        Game game = activeGames.get(gameId);
        boolean moveResult = game.makeMove(playerId);

        if (moveResult) {
            diceHolders.remove(gameId);
        }

        return moveResult;
    }

    @Override
    public List<String> getPlayerGameHistory(int playerId) {
        return playerHistory.getPlayerHistory(playerId);
    }

    private void validateGameCreationParameters(int boardSize, List<Integer> playerIds) {
        if (boardSize <= 0) {
            throw new IllegalArgumentException("Board size must be positive");
        }
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalArgumentException("Player IDs cannot be null or empty");
        }
    }

    private void validatePlayersAvailability(List<Integer> playerIds) {
        boolean playerInGame = activeGames.values().stream()
            .filter(game -> !game.getState().isEnded())
            .anyMatch(game -> hasCommonPlayers(game, playerIds));

        if (playerInGame) {
            throw new IllegalStateException("A player is already in an ongoing game");
        }
    }

    private boolean hasCommonPlayers(Game game, List<Integer> playerIds) {
        return game.getPlayers().stream()
            .map(Player::getId)
            .anyMatch(playerIds::contains);
    }

    private void acquirePlayerLocks(List<Integer> playerIds) {
        playerIds.stream()
            .map(id -> playerLocks.computeIfAbsent(id, k -> new ReentrantLock()))
            .forEach(Lock::lock);
    }

    private void releasePlayerLocks() {
        playerLocks.values().forEach(Lock::unlock);
    }

    Map<String, Game> getActiveGames() {
        return activeGames;
    }
}