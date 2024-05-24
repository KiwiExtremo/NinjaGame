package com.example.ninjagame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.Vector;

public class GameView extends View {
    SoundPool soundPool;
    int llancamentSoundID, explosioSoundID;
    private GameActivity parent;
    private String username;
    private int score = 0;
    private float mX=0, mY=0;
    private boolean launch = false;
    private Drawable drwNinja, drwKnife, drwEnemy;
    private Drawable drwEnemyPieces[] = new Drawable[8];

    ////// NINJA //////

    private boolean alive = true;
    private Paint usernamePaint, scorePaint;
    private Graphics ninjaPlayable;
    private int ninjaRotation;
    private float ninjaAcceleration;
    private static final int INC_ROTATION = 5;
    private static final float INC_ACCELERATION = 0.5f;

    ////// THREAD I TEMPS //////
    private ThreadGame thread = new ThreadGame();
    private static int PROCESS_PERIOD = 50;
    private long lastProcess = 0;

    ////// LLANÇAMENT //////
    private Graphics knife;
    private static int INC_KNIFE_VELOCITY = 12;
    private boolean activeKnife =false;
    private int knifeTime;
    private int chosenEnemies;
    private SharedPreferences prefs, scoresPrefs;
    private Vector<Graphics> enemies;
    private int screenWidth, screenHeight;
    private boolean win = false;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeDataAndSharedPrefs();
        fetchDrawables(context);

        ninjaPlayable = new Graphics(this, drwNinja);
        enemies = new Vector<>();

        for (int i = 0; i < chosenEnemies; i++) {
            Graphics enemy = new Graphics(this, drwEnemy);

            enemy.setIncY(Math.random() * 4 - 2);
            enemy.setIncX(Math.random() * 4 - 2);
            enemy.setAngle((int) (Math.random() * 360));
            enemy.setRotation((int) (Math.random() * 8 - 4));

            enemies.add(enemy);
        }

        knife = new Graphics(this, drwKnife);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                launch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);

                if (dy < 6 && dx > 6){
                    ninjaRotation = Math.round((x - mX) / 2);
                    launch = false;

                } else if (dx < 6 && dy > 6){
                    ninjaAcceleration = Math.round((mY - y) / 25);
                    launch = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                ninjaRotation = 0;
                ninjaAcceleration = 0;

                if (launch) {
                    throwKnife();
                }

                break;
        }
        mX=x; mY=y;
        return true;
    }

    @Override
    public boolean onKeyDown(int codiTecla, KeyEvent event) {
        super.onKeyDown(codiTecla, event);

        boolean processed = true;

        switch (codiTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                ninjaAcceleration = INC_ACCELERATION;
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                ninjaAcceleration = -INC_ACCELERATION;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                ninjaRotation = -INC_ROTATION;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                ninjaRotation = INC_ROTATION;
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                //DisparaGanivet();
                break;

            default:
                processed = false;
                break;
        }
        return processed;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);

        boolean processed = true;

        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                ninjaAcceleration = 0;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                ninjaRotation = 0;
                break;

            default:
                processed = false;
                break;
        }
        return processed;
    }

    @Override
    protected void onSizeChanged(int width, int height, int prev_width, int prev_height) {
        super.onSizeChanged(width, height, prev_width, prev_height);

        screenWidth = width;
        screenHeight = height;

        ninjaPlayable.setPosX((float) width / 2);
        ninjaPlayable.setPosY((float) height / 2);

        for (Graphics enemy : enemies) {
            do{
                enemy.setPosX(Math.random() * (width - enemy.getWidth()));
                enemy.setPosY(Math.random( )* (height - enemy.getHeight()));

            } while(enemy.distance(ninjaPlayable) < (float) (width + height) / 5);
        }

        lastProcess = System.currentTimeMillis();
        thread.start();

        fetchUsername();
        createNewPaints();
    }

    @Override
    synchronized protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (alive) {
            ninjaPlayable.drawGraphic(canvas);
        }
        
        for (Graphics enemy : enemies) {
            enemy.drawGraphic(canvas);
        }

        if (activeKnife) {
            knife.drawGraphic(canvas);
        }

        canvas.drawText(getContext().getString(R.string.game_text_username, username), (float) screenWidth / 40, (float) screenHeight / 8, usernamePaint);
        canvas.drawText(getContext().getString(R.string.game_text_score, score), screenWidth - ((float) screenWidth / 40), (float) screenHeight / 8, scorePaint);
    }

    synchronized protected void updateMovement() {
        long instant_actual = System.currentTimeMillis();

        if (lastProcess + PROCESS_PERIOD > instant_actual) {
            return;
        }

        double retard = (float) (instant_actual - lastProcess) / PROCESS_PERIOD;

        lastProcess = instant_actual;

        ninjaPlayable.setAngle((int) (ninjaPlayable.getAngle() + ninjaRotation * retard));

        double nIncX = ninjaPlayable.getIncX() + ninjaAcceleration * Math.cos(Math.toRadians(ninjaPlayable.getAngle())) * retard;
        double nIncY = ninjaPlayable.getIncY() + ninjaAcceleration * Math.sin(Math.toRadians(ninjaPlayable.getAngle())) * retard;

        if (Math.hypot(nIncX,nIncY) <= Graphics.MAX_SPEED){
            ninjaPlayable.setIncX(nIncX);
            ninjaPlayable.setIncY(nIncY);
        }

        ninjaPlayable.increasePosition(retard);

        for(Graphics enemy : enemies) {
            enemy.increasePosition(retard);
        }

        if (activeKnife) {
            knife.increasePosition(retard);
            knifeTime -=retard;

            if (knifeTime < 0) {
                activeKnife = false;

            } else {
                for (int i = 0; i < enemies.size(); i++) {
                    if (knife.isColliding(enemies.elementAt(i))) {
                        destroyEnemy(i);
                        updateScore();

                        break;
                    }
                }
            }

            for (int i = 0; i < enemies.size(); i++) {
                if (ninjaPlayable.isColliding(enemies.elementAt(i))) {
                    destroyPlayer();
                }
            }
        }


    }

    private void destroyPlayer() {
        soundPool.play(explosioSoundID, 1, 1, 1, 0, 1);

        alive = false;

        saveScoreToSharedPrefs();
    }

    private void destroyEnemy(int i) {
        soundPool.play(explosioSoundID, 1, 1, 1, 0, 1);

        int numParts = 3;

        if (enemies.get(i).getDrawable() == drwEnemy) {
            for (int n = 0; n < numParts; n++) {
                Graphics enemy = new Graphics(this, drwEnemyPieces[n]);

                enemy.setPosX(enemies.get(i).getPosX());
                enemy.setPosY(enemies.get(i).getPosY());

                enemy.setIncX(Math.random()*7-3);
                enemy.setIncY(Math.random()*7-3);

                enemy.setAngle((int) (Math.random() * 360));
                enemy.setRotation((int) (Math.random() * 8-4));

                enemies.add(enemy);
            }
        }

        enemies.remove(i);
        
        activeKnife = false;
        
        if (enemies.size() == 0) {
            win = true;
        }
    }

    private void updateScore() {
        score += 1;
    }
    private void throwKnife() {
        if (!alive) {
            return;
        }

        soundPool.play(llancamentSoundID, 1, 1, 1, 0, 1);

        knife.setPosX(ninjaPlayable.getPosX() + (float) ninjaPlayable.getWidth() / 2 - (float) knife.getWidth()/2);
        knife.setPosY(ninjaPlayable.getPosY() + (float) ninjaPlayable.getHeight() / 2 - (float) knife.getHeight()/2);

        knife.setAngle(ninjaPlayable.getAngle());

        knife.setIncX(Math.cos(Math.toRadians(knife.getAngle())) * INC_KNIFE_VELOCITY);
        knife.setIncY(Math.sin(Math.toRadians(knife.getAngle())) * INC_KNIFE_VELOCITY);

        knifeTime = (int) Math.min(this.getWidth() / Math.abs( knife.getIncX()),
                this.getHeight() / Math.abs(knife.getIncY())) - 2;

        activeKnife = true;
    }

    private void fetchDrawables(Context context) {
        String ninjaSprite = prefs.getString("chosenNinja", context.getString(R.string.preference_default_ninja_drawable));
        int ninjaID = getResources().getIdentifier(ninjaSprite, "drawable", context.getPackageName());

        drwNinja = ContextCompat.getDrawable(context, ninjaID);
        drwEnemy = ContextCompat.getDrawable(context, R.drawable.ninja_enemic);
        drwKnife = ContextCompat.getDrawable(context, R.drawable.ganivet);

        drwEnemyPieces[0] = ContextCompat.getDrawable(context, R.drawable.cap_ninja);
        drwEnemyPieces[1] = ContextCompat.getDrawable(context, R.drawable.cos_ninja);
        drwEnemyPieces[2] = ContextCompat.getDrawable(context, R.drawable.cua_ninja);
    }

    private void initializeDataAndSharedPrefs() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().build();

        soundPool = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(audioAttributes).build();

        llancamentSoundID = soundPool.load(getContext(), R.raw.llancament, 1);
        explosioSoundID = soundPool.load(getContext(), R.raw.explosio, 1);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        chosenEnemies = Integer.parseInt(prefs.getString("chosenEnemies", "5"));
    }

    private void saveScoreToSharedPrefs() {
        scoresPrefs = parent.getSharedPreferences(getContext().getString(R.string.preference_scores_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = scoresPrefs.edit();
        int last = 1, playerScore = 0;

        while (playerScore != -1) {
            playerScore = scoresPrefs.getInt("score" + last, -1);

            if (playerScore != -1) {
                last++;
            }
        }

        editor.putString("player" + last, username);
        editor.putInt("score" + last, score);

        editor.apply();
    }

    private void createNewPaints() {
        usernamePaint = new Paint();
        usernamePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        usernamePaint.setTextSize((float) screenHeight / 10);

        scorePaint = new Paint();
        scorePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        scorePaint.setTextAlign(Paint.Align.RIGHT);
        scorePaint.setTextSize((float) screenHeight / 10);
    }
    public void fetchUsername() {
        Bundle bundle = parent.getIntent().getExtras();
        username = bundle.getString("username");
    }

    public void setParent(GameActivity parent) {
        this.parent = parent;
    }

    class ThreadGame extends Thread {
        @Override
        public void run() {

            while (alive) {
                updateMovement();
            }
        }
    }
}
