package com.example.snake;

import java.util.HashMap;

public class Snake {
    public static final int EASY = 0, NORMAL = 1, HARD = 2, UBER = 3;
    public static final int DIR_UP = 0, DIR_RIGHT = 1, DIR_DOWN = 2, DIR_LEFT = 3;
    public static final HashMap<Integer, String> difficulties;

    static {
        difficulties = new HashMap<>();
        difficulties.put(EASY, "easy");
        difficulties.put(NORMAL, "normal");
        difficulties.put(HARD, "hard");
        difficulties.put(UBER, "uber");
    }

    public static final boolean EMPTY = false, SNAKE = true;

    public int snakeX, snakeY, direction, nextDirection;

    public Snake(int snakeXPos, int snakeYPos, int snakeDirection){
        snakeX = snakeXPos;
        snakeY = snakeYPos;
        direction = snakeDirection;
        nextDirection = direction;
    }
}