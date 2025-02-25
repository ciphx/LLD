package com.experiments.snakeAndLadder.service;

import java.util.List;
import java.util.Map;

public interface GameApplication {
    String createGame(int boardSize, Map<Integer, Integer> snakes, 
                     Map<Integer, Integer> ladders, List<Integer> playerIds);
    
    boolean rollDiceAndMove(String gameId, int playerId);
    
    boolean holdDice(String gameId, int playerId);
    
    List<String> getPlayerGameHistory(int playerId);
} 