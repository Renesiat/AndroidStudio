package com.example.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    public class GameActivity extends AppCompatActivity {

        private int[][] cells = new int[4][4];
        private TextView[][] tvCells = new TextView[4][4];
        private final Random random = new Random();
        private Animation spawnCellAnimation;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game);
            this.spawnCellAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.spawn_cell);
            this.spawnCellAnimation.reset();

            for(int i = 0; i < 4; i++){
                for (int j = 0; j < 4; j++){
                    this.tvCells[i][j] = findViewById(getResources().getIdentifier("game_cell_" + i + j, "id", getPackageName()));
                }

            findViewById( R.id.game_layout )
                    .setOnTouchListener( new OnSwipeListener( GameActivity.this ) {
                        @Override
                        public void onSwipeLeft() {
                            if(moveLeft()) spawnCell();
                            else  Toast.makeText(GameActivity.this, "Not left move", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSwipeRight() {
                            if(moveRight()) spawnCell();
                            else  Toast.makeText(GameActivity.this, "Not right move", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSwipeTop() {
                            Toast.makeText(GameActivity.this, "Top", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSwipeBottom() {
                            Toast.makeText(GameActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                        }

                    } ) ;
        }     spawnCell();
        }

        private void showField(){

            Resources resources = getResources();

            for ( int i = 0; i < 4; i++){
                for( int j = 0; j < 4; j++){
                    tvCells[i][j].setText(String.valueOf(cells[i][j]));
                    tvCells[i][j].setTextAppearance(resources.getIdentifier("GameCell_" + cells[i][j], "style", getPackageName()));

                    tvCells[i][j].setBackgroundColor(
                            resources.getColor(resources.getIdentifier(
                                    "game_bg_" + cells[i][j],"color", getPackageName()), getTheme()
                            )
                    );
                }
            }
        }
        private boolean spawnCell(){

            List<Integer> freeCellIndexes = new ArrayList<>();

            for(int i = 0; i < 4; i++){
                for(int j  = 0; j < 4; j++){
                    if(cells[i][j] == 0){
                        freeCellIndexes.add(i * 10 + j);
                    }
                }
            }

            int cnt = freeCellIndexes.size();

            if(cnt == 0)
                return false;


            int randIndex = random.nextInt(cnt);
            Log.wtf("rnd",String.valueOf(randIndex));

            Log.wtf("xx",String.valueOf(freeCellIndexes.get(randIndex)));
            int x = freeCellIndexes.get(randIndex) / 10;
            int y = freeCellIndexes.get(randIndex) % 10;


            Log.wtf("x",String.valueOf(x));
            Log.wtf("y",String.valueOf(y));

            cells[x][y] = random.nextInt(10) < 9 ? 2 : 4;

            tvCells[x][y].startAnimation(spawnCellAnimation);

            showField();

            return true;
        }

        private boolean moveLeft(){
            boolean result = false;
            boolean needRepeat;

            for( int i = 0; i < 4; i++){
                do {
                    needRepeat = false;
                    for (int j = 0; j < 3; j++) {
                        if (cells[i][j] == 0) {
                            for (int k = j + 1; k < 4; k++) {
                                if (cells[i][k] != 0) {
                                    cells[i][j] = cells[i][k];
                                    cells[i][k] = 0;
                                    needRepeat = true;
                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }while (needRepeat);
            }

            return result;
        }
        private boolean moveRight(){
            boolean result = false;
            boolean needRepeat;

            for( int i = 3; i >= 0; i--){
                do {
                    needRepeat = false;
                    for (int j = 3; j >= 0; j--) {
                        if (cells[i][j] == 0) {
                            for (int k = j - 1; k >= 0; k--) {
                                if (cells[i][k] != 0) {
                                    cells[i][j] = cells[i][k];
                                    cells[i][k] = 0;
                                    needRepeat = true;
                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }while (needRepeat);
            }

            return result;
    }
    }

