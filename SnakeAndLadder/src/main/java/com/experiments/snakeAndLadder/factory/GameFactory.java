package com.experiments.snakeAndLadder.factory;

import com.experiments.snakeAndLadder.model.Game;

import java.util.List;
import java.util.Map;


public interface GameFactory {
    Game createGame(int boardSize, Map<Integer, Integer> snakes,
                    Map<Integer, Integer> ladders, List<Integer> playerIds);
} 