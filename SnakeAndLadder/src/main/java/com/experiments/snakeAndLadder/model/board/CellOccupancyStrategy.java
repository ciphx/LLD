package com.experiments.snakeAndLadder.model.board;

import java.util.List;

import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.model.Position;

public interface CellOccupancyStrategy {
    boolean canOccupyCell(Position position, List<Player> currentOccupants, Player newPlayer);
}