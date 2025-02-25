package com.experiments.snakeAndLadder.model.board;

import lombok.Getter;

@Getter
public class Cell {
    private final int position;
    private final CellType type;
    private final int destination;

    public enum CellType {
        NORMAL,
        SNAKE,
        LADDER
    }

    public Cell(int position) {
        this.position = position;
        this.type = CellType.NORMAL;
        this.destination = position;
    }

    public Cell(int position, CellType type, int destination) {
        this.position = position;
        this.type = type;
        this.destination = destination;
    }

    public boolean hasTransition() {
        return type != CellType.NORMAL;
    }
}