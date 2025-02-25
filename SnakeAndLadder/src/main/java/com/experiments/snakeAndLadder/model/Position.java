package com.experiments.snakeAndLadder.model;


public record Position(int value) {
    public Position {
        if (value < 0) {
            throw new IllegalArgumentException("Position cannot be negative");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Position position && position.value == this.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}