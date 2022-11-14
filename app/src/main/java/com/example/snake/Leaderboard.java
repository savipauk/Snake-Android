package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        DatabaseManager db = DatabaseManager.getInstance(this);

        ArrayList<Score> scores = db.getScores();

        String myscore = "";
        ListView board = findViewById(R.id.board);
        ArrayList<String> myscores = new ArrayList<>();
        myscores.add("player name - score - difficulty");

        for(Score score : scores){
            myscore = score.getPlayerName() + " - " + score.getScore() + " - " + Snake.difficulties.get(score.getDifficulty());
            myscores.add(myscore);
        }

        board.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myscores));


    }
}