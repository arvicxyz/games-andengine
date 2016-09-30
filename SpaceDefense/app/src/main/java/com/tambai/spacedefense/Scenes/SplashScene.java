package com.tambai.spacedefense.Scenes;

import com.tambai.spacedefense.Managers.SceneManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

public class SplashScene extends BaseScene
{
    private Font mFont = resourceManager.splashFont;

    private Text junixText;
    private Text gamesText;
    private Text presentsText;
    private Text defendTheEarthText;
    private Text loadingText;

    @Override
    public void createScene()
    {
        this.getBackground().setColor(Color.WHITE);

        TimerHandler timer = new TimerHandler(1f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                showJunixGamesLogo();
            }
        });

        this.registerUpdateHandler(timer);
    }

    @Override
    public void onBackKeyPressed()
    {
        return;
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        return SceneManager.SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene()
    {
        junixText.detachSelf();
        junixText.dispose();

        gamesText.detachSelf();
        gamesText.dispose();

        presentsText.detachSelf();
        presentsText.dispose();

        defendTheEarthText.detachSelf();
        defendTheEarthText.dispose();

        loadingText.detachSelf();
        loadingText.dispose();

        this.detachSelf();
        this.dispose();
    }

    private void showJunixGamesLogo()
    {
        // creating "Junix Games" text objects
        junixText = new Text(-mFont.getTexture().getWidth() / 2,
                camera.getHeight() / 2, mFont, "JUNIX", vbom);
        gamesText = new Text(camera.getWidth() + (mFont.getTexture().getWidth() / 2),
                camera.getHeight() / 2, mFont, "GAMES", vbom);

        // attaching the "Junix Games" text object
        attachChild(junixText);
        attachChild(gamesText);

        // moving towards each other MoveXModifiers
        MoveXModifier junixMoveMod = new MoveXModifier(0.5f, junixText.getX(),
                (camera.getWidth() / 2) - (mFont.getTexture().getWidth() / 2));
        MoveXModifier gamesMoveMod = new MoveXModifier(0.5f, gamesText.getX(),
                (camera.getWidth() / 2) + (mFont.getTexture().getWidth() / 2));

        junixText.registerEntityModifier(junixMoveMod);
        gamesText.registerEntityModifier(gamesMoveMod);

        // timer: after 0.8 sec when they appeared the words changes color
        TimerHandler timerIn = new TimerHandler(0.8f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                changeJunixGamesColor();
            }
        });

        this.registerUpdateHandler(timerIn);

        // timer: after 2.5 sec when they appeared the words fade out
        TimerHandler timerOut = new TimerHandler(2.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                fadeOutJunixGames();
                fadeInPresents();
            }
        });

        this.registerUpdateHandler(timerOut);
    }

    private void changeJunixGamesColor()
    {
        // color changes in both words ColorModifier
        final ColorModifier junixColorMod = new ColorModifier(1.5f, Color.WHITE, Color.YELLOW);
        final ColorModifier gamesColorMod = new ColorModifier(1.5f, Color.WHITE, Color.RED);

        junixText.registerEntityModifier(junixColorMod);
        gamesText.registerEntityModifier(gamesColorMod);
    }

    private void fadeOutJunixGames()
    {
        // fade out the words
        FadeOutModifier junixFadeOut = new FadeOutModifier(0.5f);
        FadeOutModifier gamesFadeOut = new FadeOutModifier(0.5f);

        junixText.registerEntityModifier(junixFadeOut);
        gamesText.registerEntityModifier(gamesFadeOut);
    }

    private void fadeInPresents()
    {
        // show "presents" word after fade out
        presentsText = new Text(camera.getWidth() / 2, camera.getHeight() / 2, mFont, "presents", vbom);
        defendTheEarthText = new Text(camera.getWidth() / 2, camera.getHeight() / 2, mFont, "Defend\nThe\nEarth", vbom);

        presentsText.setScale(0.75f);

        defendTheEarthText.setHorizontalAlign(HorizontalAlign.CENTER);
        defendTheEarthText.setColor(Color.BLACK);

        loadingText = new Text(camera.getWidth() / 2,
                50, mFont, "loading...", vbom);
        loadingText.setScale(0.5f);

        // fade in the word
        final FadeInModifier presentsFadeIn = new FadeInModifier(0.5f);

        // fade out the word
        final FadeOutModifier presentsfadeOut = new FadeOutModifier(0.5f);

        // timer: after 0.8 sec when they appeared the words fades in
        TimerHandler timerIn = new TimerHandler(0.8f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                attachChild(presentsText);
                presentsText.registerEntityModifier(presentsFadeIn);
            }
        });

        this.registerUpdateHandler(timerIn);

        // timer: after 2.5 sec when they appeared the word fades out
        TimerHandler timerOut = new TimerHandler(2.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                presentsText.registerEntityModifier(presentsfadeOut);
            }
        });

        this.registerUpdateHandler(timerOut);

        // timer: after 3.5 sec when they appeared the title fades in
        TimerHandler showGameTitleTimer = new TimerHandler(3.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                attachChild(defendTheEarthText);
            }
        });

        this.registerUpdateHandler(showGameTitleTimer);

        // timer: after 4.5 sec when they appeared the title fades in
        TimerHandler showloadingTextTimer = new TimerHandler(4.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                attachChild(loadingText);
            }
        });

        this.registerUpdateHandler(showloadingTextTimer);

        // timer: after 6.5 sec when they appeared the title fades in
        TimerHandler moveToNextSceneTimer = new TimerHandler(6.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                SceneManager.getInstance().createMenuScene();
            }
        });

        this.registerUpdateHandler(moveToNextSceneTimer);
    }
}
