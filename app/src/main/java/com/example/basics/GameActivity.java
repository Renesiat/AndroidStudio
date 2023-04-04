package com.example.basics;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

    public class GameActivity extends AppCompatActivity {

        private int[][] cells = new int[4][4];
        private TextView[][] tvCells = new TextView[4][4];
        private int[][] cellsCopy = new int[4][4];
        private TextView[][] tvCellsCopy = new TextView[4][4];
        private int scoreCopy;
        private final Random random = new Random();
        private Animation spawnCellAnimation;
        private int score;
        private TextView tvScore;
        private int bestScore;
        private TextView tvBestScore;
        private final String bestScoreFilename = "best_score.txt";

        private boolean isContinuePlaying;
        private void btnNewGameClick(View v){
            showNewGameDialog();
        }
        private void btnUndoClick(View v){
            loadFiledFromCopy();
        }
        //endregion

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game);


            tvScore = findViewById(R.id.tv_score);
            bestScore = loadBestScore();
            tvBestScore = findViewById(R.id.tv_best_score);
            tvBestScore.setText(getString(R.string.tv_best_score_pattern, bestScore));


            spawnCellAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.spawn_cell);
            spawnCellAnimation.reset();

            for(int i = 0; i < 4; i++){
                for (int j = 0; j < 4; j++){
                    this.tvCells[i][j] = findViewById(getResources().getIdentifier("game_cell_" + i + j, "id", getPackageName()));
                }
            }
            findViewById(R.id.game_layout).setOnTouchListener(new OnSwipeListener(GameActivity.this){
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
                    if(moveTop()) spawnCell();
                    else  Toast.makeText(GameActivity.this, "Not top move", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeBottom() {
                    Toast.makeText(GameActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                    if(moveBottom()) spawnCell();
                    else  Toast.makeText(GameActivity.this, "Not bottom move", Toast.LENGTH_SHORT).show();
                }
            });
            findViewById(R.id.game_start_new).setOnClickListener(this::btnNewGameClick);
            findViewById(R.id.game_undo).setOnClickListener(this::btnUndoClick);

            newGame();
        }

        private void newGame(){
            score = 0;
            scoreCopy = 0;
            isContinuePlaying = false;

            for(int i = 0; i < 4; i++){
                for(int j = 0; j < 4; j++){
                    cells[i][j] = 0;
                }
            }

            findViewById(R.id.game_undo).setEnabled(false);

            spawnCell();
        }
        private void setFiledCopy(){
            for(int i = 0; i < 4; i++){
                cellsCopy[i] = cells[i].clone();
            }
            scoreCopy = score;
            undoButtonState(true);
        }
        private void loadFiledFromCopy(){
            for(int i = 0; i < 4; i++){
                cells[i] = cellsCopy[i].clone();
            }
            score = scoreCopy;

            showField();

            undoButtonState(false);
        }
        private void undoButtonState(boolean isEnable){
            findViewById(R.id.game_undo).setEnabled(isEnable);
        }

        private boolean saveBestScore(){
            try(FileOutputStream fos = openFileOutput(this.bestScoreFilename, Context.MODE_PRIVATE)){
                DataOutputStream writer = new DataOutputStream(fos);
                writer.writeInt(bestScore);
                writer.flush();
                writer.close();
            }catch (Exception ex){
                Log.d("saveSaveBestScore", ex.getMessage());
                return false;
            }
            return true;
        }
        private int loadBestScore(){
            int best = 0;
            try(FileInputStream fis = openFileInput(this.bestScoreFilename)) {
                DataInputStream reader = new DataInputStream(fis);
                best = reader.readInt();
                reader.close();
            }catch (Exception ex){
                Log.d("loadBestScore", ex.getMessage());
                return 0;
            }
            return best;
        }

        private boolean isWin(){
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < 4; j++){
                    if(cells[i][j] == 2048){
                        return true;
                    }
                }
            }
            return false;
        }
        private void showWinDialog(){
            new AlertDialog.Builder(GameActivity.this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                    .setTitle(R.string.win_title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(R.string.win_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.win_btn_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isContinuePlaying = true;
                        }
                    }).setNegativeButton(R.string.win_btn_exit  , (dialog,whichButton) ->{
                        finish();
                    })
                    .setNeutralButton(R.string.win_btn_new_game, (dialog, whichButton) -> {
                        newGame();
                    })
                    .show();

        }
        //endregion

        //region StartNewGameSystem
        private void showNewGameDialog(){
            new AlertDialog.Builder(GameActivity.this, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                    .setTitle(R.string.new_game_title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(R.string.new_game_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.new_game_btn_Yes, (dialog, whichButton) ->{
                        newGame();
                    }).setNegativeButton(R.string.new_game_btn_No  , (dialog,whichButton) ->{
                        isContinuePlaying = true;
                    })
                    .show();

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
            setFiledCopy();
            boolean result = false;
            boolean needRepeat;

            for( int i = 0; i < 4; i++){
                do {
                    needRepeat = false;
                    for (int j = 3; j > 0; --j) {
                        if (cells[i][j] == 0) {
                            for (int k = j - 1; k >= 0; --k) {
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
                for(int j = 3; j > 0; --j){
                    if(cells[i][j] != 0 && cells[i][j] == cells[i][j - 1]){
                        cells[i][j] *= 2;
                        for(int k = j - 1; k > 0; --k){
                            cells[i][k] = cells[i][k - 1];
                        }
                        cells[i][0] = 0;
                        result = true;
                        score += cells[i][j];
                    }
                }
            }

            return result;
        }
        private boolean moveTop(){

            setFiledCopy();

            boolean result = false;
            boolean needRepeat;

            for( int i = 0; i < 4; i++){
                do {
                    needRepeat = false;
                    for (int j = 0; j < 3; j++) {
                        if (cells[j][i] == 0) {
                            for (int k = j + 1; k < 4; k++) {
                                if (cells[k][i] != 0) {
                                    cells[j][i] = cells[k][i];
                                    cells[k][i] = 0;
                                    needRepeat = true;
                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }while (needRepeat);

                for(int j = 0; j < 3; j++){
                    if(this.cells[j][i] != 0 && cells[j][i] == cells[j+1][i]){
                        cells[j][i] *= 2;
                        for(int k = j + 1; k < 3; k++){
                            cells[k][i] = cells[k+1][i];
                        }
                        cells[3][i] = 0;
                        result = true;
                        score += cells[j][i];
                    }
                }

            }

            return result;
        }
        private boolean moveBottom(){

            this.setFiledCopy();

            boolean result = false;
            boolean needRepeat;

            for( int i = 0; i < 4; i++){
                do {
                    needRepeat = false;
                    for (int j = 3; j > 0; --j) {
                        if (cells[j][i] == 0) {
                            for (int k = j - 1; k >= 0; --k) {
                                if (cells[k][i] != 0) {
                                    cells[j][i] = cells[k][i];
                                    cells[k][i] = 0;
                                    needRepeat = true;
                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }while (needRepeat);

                for(int j = 3; j > 0; --j){
                    if(cells[j][i] != 0 && cells[j][i] == cells[j - 1][i]){
                        cells[j][i] *= 2;
                        for(int k = j - 1; k > 0; --k){
                            cells[k][i] = cells[k - 1][i];
                        }
                        cells[0][i] = 0;
                        result = true;
                        score += cells[j][i];
                    }
                }

            }

            return result;
    }
    }

