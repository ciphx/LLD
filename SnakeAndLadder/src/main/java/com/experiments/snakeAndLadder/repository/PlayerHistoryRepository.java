package com.experiments.snakeAndLadder.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHistoryRepository {
    private final Map<Integer, List<String>> playerGameHistory = new ConcurrentHashMap<>();

    public void recordGame(List<Integer> playerIds, String gameId) {
        playerIds.forEach(playerId -> 
            playerGameHistory.computeIfAbsent(playerId, k -> new ArrayList<>())
                           .add(gameId)
        );
    }

    public List<String> getPlayerHistory(int playerId) {
        return new ArrayList<>(playerGameHistory.getOrDefault(playerId, new ArrayList<>()));
    }
} 