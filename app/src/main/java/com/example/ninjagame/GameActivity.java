package com.example.ninjagame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private boolean isMusic;
    private SharedPreferences pref;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        gameView = findViewById(R.id.GameView);
        gameView.setParent(this);

        getFromSharedPrefs();
        updateBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBackgroundMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    private void getFromSharedPrefs() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mp = MediaPlayer.create(GameActivity.this, R.raw.game_music);
    }

    private void updateBackgroundMusic() {
        isMusic = pref.getBoolean("checkGameMusic", true);

        if (isMusic) {
            mp.setLooping(true);
            mp.start();

        } else if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }


    private void showDialogGiveUp() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_give_up_title));
        builder.setMessage(getString(R.string.dialog_give_up_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_give_up_positive), (dialog, which) -> {
            gameView.saveScoreToSharedPrefs(-1);
        });

        builder.setNegativeButton(getString(R.string.dialog_give_up_negative), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogGameOver(int endCode, int score) {
        runOnUiThread(() -> {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_game_over_title));

            if (endCode == 0) {
                builder.setMessage(getString(R.string.dialog_game_over_body_win, score));

            } else {
                builder.setMessage(getString(R.string.dialog_game_over_body_lose, score));
            }
            // add the buttons
            builder.setPositiveButton(getString(R.string.dialog_game_over_positive), (dialog, which) -> {
                finish();
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        showDialogGiveUp();
    }

    public void finishGame(int endCode, int score) {

        showDialogGameOver(endCode, score);
    }
}