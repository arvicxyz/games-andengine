package com.tambai.tapmania.Scenes;

import android.content.Context;

import com.tambai.tapmania.Managers.SceneManager;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

public class GameScreenScene extends BaseScene implements IOnSceneTouchListener
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private HUD gameHud;

    private Text scoreText;
    private int score;

    private Text submitScoreText;
    private int best;

    private Text speedText;
    private float speed;

    private Text timeText;
    private float time;

    private Text gameOverText;
    private TimerHandler timer;
    private boolean isGameOver = false;

    //-----------------------------------------
    // GAME SCREEN SCENE
    //-----------------------------------------

    @Override
    public void createScene()
    {
        score = 0;
        best = getBestScore();
        speed = 0.0f;
        time = 0.0f;

        this.getBackground().setColor(Color.GREEN);
        createHud();

        timer = new TimerHandler(1.0f, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                time += 1.0f;
                timeText.setText("TIME: " + String.valueOf((int) time).toString() + " sec");
            }
        });

        this.setOnSceneTouchListener(this);
        engine.registerUpdateHandler(timer);
    }

    @Override
    public void onBackKeyPressed()
    {
        SceneManager.getInstance().loadMenuScene();
        resetGame();
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        return SceneManager.SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
        camera.setHUD(null);
        camera.setCenter(camera.getWidth() / 2, camera.getHeight() / 2);
    }

    private void createHud()
    {
        gameHud = new HUD();

        scoreText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.9f,
                resourceManager.gameScoreFont, "0123456789", vbom);
        scoreText.setText(String.valueOf(score).toString());
        gameHud.attachChild(scoreText);

        speedText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.3f,
                resourceManager.gameSpeedTimeFont, "01234.56789 taps/sec", vbom);
        speedText.setText(String.valueOf(speed).toString() + " taps/sec");
        gameHud.attachChild(speedText);

        timeText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.25f,
                resourceManager.gameSpeedTimeFont, "TIME: 0123456789 sec", vbom);
        timeText.setText("TIME: " + String.valueOf((int) time).toString() + " sec");
        timeText.setScale(1.5f);
        gameHud.attachChild(timeText);

        submitScoreText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.1f,
                resourceManager.gameSpeedTimeFont, "Sumbit Score", vbom)
        {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                                         float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionUp())
                {
                    if (!isGameOver)
                    {
                        isGameOver = true;
                        showGameOver();
                        submitScoreText.setText("Go To Main Menu");
                        setOnSceneTouchListener(null);
                        engine.unregisterUpdateHandler(timer);
                    }
                    else
                    {
                        SceneManager.getInstance().loadMenuScene();
                        resetGame();
                    }
                }
                else
                {
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        this.registerTouchArea(submitScoreText);
        submitScoreText.setScale(2f);
        submitScoreText.setColor(Color.RED);
        gameHud.attachChild(submitScoreText);

        camera.setHUD(gameHud);
    }

    private void addScore(int i)
    {
        score += i;
        scoreText.setText(String.valueOf(score).toString());
    }

    private void resetGame()
    {
        score = 0;
        speed = 0.0f;
        time = 0.0f;
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {
        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
        {
            addScore(1);
            speed = score / time;
            speedText.setText(String.valueOf(speed).toString() + " taps/sec");

            return true;
        }

        return false;
    }

    private int getBestScore()
    {
        return activity.getPreferences(Context.MODE_PRIVATE).getInt("bestScore", 0);
    }

    private void setBestScore(int bestScore)
    {
        this.activity.getPreferences(Context.MODE_PRIVATE).edit().putInt("bestScore", bestScore).apply();
    }

    public void showGameOver()
    {
        String gameOverString;

        if (score > best)
        {
            best = score;
            setBestScore(best);
            gameOverString = "new best score!\nscore: " + score + "\nbest: " + best;
        }
        else
        {
            gameOverString = "nice game!\nscore: " + score + "\nbest: " + best;
        }

        gameOverText = new Text(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.gameSpeedTimeFont, gameOverString, vbom);
        gameOverText.setScale(2f);
        gameOverText.setColor(Color.WHITE);
        attachChild(gameOverText);
    }
}
