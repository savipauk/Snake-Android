package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainMenu extends AppCompatActivity {
    public static boolean soundCheck = true;
    ToggleButton tb;

    int difficulty = Snake.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tb = findViewById(R.id.soundToggle);
    }

    protected void onResume() {
        super.onResume();
        tb.setChecked(Game.soundCheck);
        soundCheck = Game.soundCheck;
    }

    public void playGame(View view) {
        String playerName = ((EditText)findViewById(R.id.playerName)).getText().toString();

        Intent game = new Intent(MainMenu.this, Game.class);
        game.putExtra("playerName", playerName);
        game.putExtra("difficulty", difficulty);
        startActivity(game);

    }

    public void quitGame(View view) {
        finish();
    }

    public void soundSwitch(View view) {
        soundCheck = tb.isChecked();
    }

    public void radioButtonPress(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        if(!checked) return;

        switch(view.getId()){
            case R.id.easy:
                difficulty = Snake.EASY;
                break;
            case R.id.normal:
                difficulty = Snake.NORMAL;
                break;
            case R.id.hard:
                difficulty = Snake.HARD;
                break;
            case R.id.uber:
                difficulty = Snake.UBER;
                break;
        }
    }

    public void leaderboard(View view) {
        Intent game = new Intent(MainMenu.this, Leaderboard.class);
        startActivity(game);
    }
}