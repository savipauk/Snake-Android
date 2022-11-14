package com.example.snake;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class Game extends AppCompatActivity {
    int score = 1;
    String playerName = "";

    LinkedList<Snake> snake = new LinkedList<>();
    int snakeHead = 0;

    int snakeHeadColor = Color.GREEN;
    int snakeBodyColor = Color.parseColor("#11AA22");
    int snakeDirection = 0;

    int foodX;
    int foodY;

    int gridSize = 9;
    boolean[][] grid = new boolean[gridSize][gridSize];

    String gameBackgroundColorString = "#2f2f30";
    int gameBackgroundColor = Color.parseColor(gameBackgroundColorString);

    int bmSize = 450;
    int multiplier = 50;
    int gameSpeed; // Time between updates in ms
    HashMap<Integer, Integer> gameDifficulty = new HashMap<>();
    int difficulty;

    Bitmap bitmap = Bitmap.createBitmap(bmSize, bmSize, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    Random ran = new Random();

    TextView scoreLabel;
    ToggleButton tb;
    public static boolean soundCheck = true;
    boolean gameFinished = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialization
        gameDifficulty.put(Snake.EASY, 500);
        gameDifficulty.put(Snake.NORMAL, 375);
        gameDifficulty.put(Snake.HARD, 250);
        gameDifficulty.put(Snake.UBER, 100);

        // Get intent extras
        difficulty = getIntent().getIntExtra("difficulty", 1);
        playerName = getIntent().getStringExtra("playerName");

        gameSpeed = gameDifficulty.get(difficulty);

        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                grid[i][j] = Snake.EMPTY;
            }
        }

        snake.add(new Snake(4, 4, Snake.DIR_UP));
        addSnake();
        addSnake();
        spawnFood();

        scoreLabel = findViewById(R.id.score);

        tb = findViewById(R.id.soundToggle);
        tb.setChecked(MainMenu.soundCheck);
        soundCheck = MainMenu.soundCheck;

        final ImageButton left = findViewById(R.id.left);
        final ImageButton up = findViewById(R.id.up);
        final ImageButton down = findViewById(R.id.down);
        final ImageButton right = findViewById(R.id.right);

        //LEFT
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    left.setImageResource(R.drawable.trianglepressed);

                    if(snake.get(snakeHead).direction != Snake.DIR_RIGHT)
                        snakeDirection = Snake.DIR_LEFT; //snake.get(snakeHead).direction = DIR_LEFT;
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    left.setImageResource(R.drawable.triangle);
                    return true;
                }
                return false;
            }
        });

        //RIGHT
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    right.setImageResource(R.drawable.trianglepressed);

                    if(snake.get(snakeHead).direction != Snake.DIR_LEFT)
                        snakeDirection = Snake.DIR_RIGHT; //snake.get(snakeHead).direction = DIR_RIGHT;
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    right.setImageResource(R.drawable.triangle);
                    return true;
                }
                return false;
            }
        });

        //UP
        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    up.setImageResource(R.drawable.trianglepressed);

                    if(snake.get(snakeHead).direction != Snake.DIR_DOWN)
                        snakeDirection = Snake.DIR_UP; //snake.get(snakeHead).direction = DIR_UP;
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    up.setImageResource(R.drawable.triangle);
                    return true;
                }
                return false;
            }
        });

        //DOWN
        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    down.setImageResource(R.drawable.trianglepressed);

                    if(snake.get(snakeHead).direction != Snake.DIR_UP)
                        snakeDirection = Snake.DIR_DOWN; //snake.get(snakeHead).direction = DIR_DOWN;
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    down.setImageResource(R.drawable.triangle);
                    return true;
                }
                return false;
            }
        });

        // Delay za canvas/game update
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateGame();
                handler.postDelayed(this, gameSpeed);
            }
        };
        handler.postDelayed(runnable, 1000);

        ImageView iv = findViewById(R.id.imageview);
        iv.setImageBitmap(bitmap);
        updateGame();
    }

    public void updateGame() {
        if(gameFinished){
            return;
        }

        if(score == 78){
            youWin();
            return;
        }

        // To prevent "input queue" bug
        snake.get(snakeHead).direction = snakeDirection;

        if(collision()){
            gameOver();
            return;
        }

        updateGrid();

        for(int i = 0; i < snake.size(); i++){
            changeSnakeDirection(i);
        }

        if(snake.get(snakeHead).snakeX == foodX && snake.get(snakeHead).snakeY == foodY){
            spawnFood();
            addSnake();
            score++;
        }

        scoreLabel.setText("" + score);

        drawBackground();
        drawFood();
        drawSnake();
    }

    public void updateGrid() {
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                grid[i][j] = Snake.EMPTY;
            }
        }

        for(Snake s : snake){
            grid[s.snakeX][s.snakeY] = Snake.SNAKE;
        }
    }

    public boolean collision() {
        Snake s = snake.get(snakeHead);
        if(s.snakeX == -1 || s.snakeX == gridSize || s.snakeY == -1 || s.snakeY == gridSize) {
            return true;
        }

        for(int i = 1; i < snake.size(); i++){
            if(s.snakeX == snake.get(i).snakeX && s.snakeY == snake.get(i).snakeY){
                return true;
            }
        }

        return false;
    }

    public void gameOver() {
        gameFinished = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game over");
        builder.setMessage("Score: " + score + "\n" +
                "Name: " + playerName);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.show();

        addPlayerToDatabase();
    }

    public void youWin() {
        gameFinished = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You win!");
        builder.setMessage("Score: " + score + "\n" +
                "Name: " + playerName);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.show();

        addPlayerToDatabase();
    }

    public void addPlayerToDatabase(){
        DatabaseManager db = DatabaseManager.getInstance(this);
        db.addScore(new Score(playerName, score, difficulty));
    }

    public void spawnFood(){
        while(true) {
            boolean check = true;
            foodX = ran.nextInt(gridSize);
            foodY = ran.nextInt(gridSize);

            for(int i = 0; i < snake.size(); i++) {
                Snake s = snake.get(i);
                if (foodX == s.snakeX && foodY == s.snakeY) {
                    check = false;
                }
            }
            if(check) break;
        }
    }

    public void changeSnakeDirection(int i){
        Snake s = snake.get(i);

        if(i != 0){
            s.direction = s.nextDirection;
            Snake previous = snake.get(i-1);
            s.nextDirection = previous.direction;
        }

        switch(s.direction){
            case Snake.DIR_LEFT:
                --s.snakeX;
                break;

            case Snake.DIR_RIGHT:
                ++s.snakeX;
                break;

            case Snake.DIR_UP:
                --s.snakeY;
                break;

            case Snake.DIR_DOWN:
                ++s.snakeY;
                break;
        }
    }

    public void addSnake(){
        Snake lastSnake = snake.getLast();
        Snake newSnake = new Snake(0, 0, lastSnake.direction);
        switch(lastSnake.direction){
            case Snake.DIR_UP:
                newSnake.snakeX = lastSnake.snakeX;
                newSnake.snakeY = lastSnake.snakeY + 1;
            break;
            case Snake.DIR_DOWN:
                newSnake.snakeX = lastSnake.snakeX;
                newSnake.snakeY = lastSnake.snakeY - 1;
            break;
            case Snake.DIR_LEFT:
                newSnake.snakeX = lastSnake.snakeX + 1;
                newSnake.snakeY = lastSnake.snakeY;
            break;
            case Snake.DIR_RIGHT:
                newSnake.snakeX = lastSnake.snakeX - 1;
                newSnake.snakeY = lastSnake.snakeY;
            break;
        }
        snake.add(newSnake);
    }

    public void drawSnake() {
        int x = snake.get(snakeHead).snakeX;
        int y = snake.get(snakeHead).snakeY;
        paint.setColor(snakeHeadColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(x * multiplier, y * multiplier, (x + 1) * multiplier, (y + 1) * multiplier, paint);
        paint.setColor(snakeBodyColor);
        for(int i = 1; i < snake.size(); i++) {
            x = snake.get(i).snakeX;
            y = snake.get(i).snakeY;
            canvas.drawRect(x * multiplier, y * multiplier, (x + 1) * multiplier , (y + 1) * multiplier, paint);
        }
    }

    public void drawFood(){
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(foodX * multiplier, foodY * multiplier, (foodX + 1) * multiplier, (foodY + 1) * multiplier, paint);
    }

    public void drawBackground() {
        canvas.drawColor(gameBackgroundColor);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(false);
        paint.setStrokeWidth(1);

        for(int i = 0; i < gridSize; i++) {
            canvas.drawLine(i * multiplier, 0, i * multiplier, canvas.getHeight(), paint);
            canvas.drawLine(0, i * multiplier, canvas.getWidth(), i * multiplier, paint);
        }
        canvas.drawLine(canvas.getWidth() - 1, 0, canvas.getWidth() - 1, canvas.getHeight(), paint);
        canvas.drawLine(0, canvas.getHeight() - 1, canvas.getWidth(), canvas.getHeight() - 1, paint);
    }


    public void toMainMenu(View view) {
        MainMenu.soundCheck = tb.isChecked();
        soundCheck = MainMenu.soundCheck;

        gameFinished = true;
        finish();
    }

    public void soundSwitch(View view) {
        MainMenu.soundCheck = tb.isChecked();
        soundCheck = MainMenu.soundCheck;
    }
}