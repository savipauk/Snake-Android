package com.example.snake;

public class Score {
    private final String playerName;
    private final int score;
    private final int difficulty;

    public Score(String _playerName, int _score, int _difficulty){
        playerName = _playerName;
        score = _score;
        difficulty = _difficulty;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getDifficulty() { return difficulty; }
}
