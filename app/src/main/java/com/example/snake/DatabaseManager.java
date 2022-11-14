package com.example.snake;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseManager {
    private DatabaseManager(){
        ;
    }

    private static DatabaseManager instance;
    private static SQLiteDatabase database;

    public static DatabaseManager getInstance(Context context){
        if(instance == null) {
            instance = new DatabaseManager();
            try {
                database = context.getApplicationContext().openOrCreateDatabase("leaderboard", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS scores (playerName VARCHAR(10), score INTEGER, difficulty INTEGER)");
            }
            catch(Exception e){
                Log.e("Error", "Error", e);
            }
        }
        return instance;
    }

    public void addScore(Score score){
        try{
            database.execSQL("INSERT INTO scores (playerName, score, difficulty) VALUES ('" + score.getPlayerName() + "', " + score.getScore() + ", " + score.getDifficulty() + ");");
        }
        catch(Exception e){
            Log.e("Error", "Error", e);
        }
    }

    public ArrayList<Score> getScores(){
        ArrayList<Score> scores = new ArrayList<>();

        try{
            Cursor c = database.rawQuery("SELECT * FROM scores", null);
            c.moveToFirst();
            if (c != null) {
                do {
                    String playerName = c.getString(c.getColumnIndex("playerName"));
                    int score = c.getInt(c.getColumnIndex("score"));
                    int difficulty = c.getInt(c.getColumnIndex("difficulty"));
                    scores.add(new Score(playerName, score, difficulty));
                } while(c.moveToNext());
            }
        }
        catch(Exception e){
            Log.e("Error", "Error", e);
        }

        return scores;
    }
}
