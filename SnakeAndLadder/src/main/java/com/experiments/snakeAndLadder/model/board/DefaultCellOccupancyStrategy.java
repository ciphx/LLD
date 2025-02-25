package com.experiments.snakeAndLadder.model.board;

import java.util.List;

import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.model.Position;

public class DefaultCellOccupancyStrategy implements CellOccupancyStrategy {
    @Override
    public boolean canOccupyCell(Position position, List<Player> currentOccupants, Player newPlayer) {
        return true; // Default implementation allows multiple players on same cell
    }
}