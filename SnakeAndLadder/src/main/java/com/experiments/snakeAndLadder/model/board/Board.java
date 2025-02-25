package com.experiments.snakeAndLadder.model.board;

import java.util.List;
import java.util.Map;

import com.experiments.snakeAndLadder.model.Player;
import com.experiments.snakeAndLadder.model.Position;
import lombok.Getter;

@Getter
public class Board {
    private final Cell[] cells;
    private final int size;
    private final CellOccupancyStrategy occupancyStrategy;

    public Board(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        this.size = size;
        this.cells = new Cell[size + 1];
        this.occupancyStrategy = new DefaultCellOccupancyStrategy();

        // Initialize all cells as normal
        for (int i = 0; i <= size; i++) {
            cells[i] = new Cell(i);
        }

        // Add snakes
        snakes.forEach((start, end) ->
                cells[start] = new Cell(start, Cell.CellType.SNAKE, end));

        // Add ladders
        ladders.forEach((start, end) ->
                cells[start] = new Cell(start, Cell.CellType.LADDER, end));
    }

    public Position getNextPosition(Position current, int steps) {
        int newPos = current.value() + steps;

        // If move would go beyond board size, stay at current position
        if (newPos >= size) {
            return current;
        }

        Cell cell = cells[newPos];
        return new Position(cell.getDestination());
    }

    public boolean canMoveToPosition(Position position, List<Player> currentOccupants, Player player) {
        // Always allow moves that stay in the same position (due to board size limit)
        if (position.value() == player.getPosition().value()) {
            return true;
        }
        return occupancyStrategy.canOccupyCell(position, currentOccupants, player);
    }

    public boolean isWinningPosition(Position position) {
        return position.value() == size - 1;
    }
}