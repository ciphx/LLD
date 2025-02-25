package com.experiments.snakeAndLadder.model;

import lombok.Setter;

@Setter
public class Player {
    private final int id;
    private Position position;

    public Player(int id) {
        this.id = id;
        this.position = new Position(0);
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

}