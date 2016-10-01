package com.tambai.fishysmack.Scenes;

import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

public class SplashScene extends BaseScene
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    // first part
    private Sprite tambaiGamesSprite;

    // second part
    private Text loadingText;
    private Sprite splashLoadingBgSprite;

    //--------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------

    public SplashScene()
    {
        createScene();
    }

    //--------------------------------------------
    // SPLASH SCENE
    //--------------------------------------------

    @Override
    public void createScene()
    {
        // sets the background color of the SPLASH SCENE to color black
        this.getBackground().setColor(Color.BLACK);

        // calls the two methods responsible for showing the two parts of the SPLASH SCENE
        showTambaiGamesLogo();
        showSplashLoading();
    }

    @Override
    public void onBackKeyPressed()
    {
        // when on the SPLASH SCENE, you cannot use back key to quit
        return;
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        // returns the type of this scene, SCENE_SPLASH
        return SceneManager.SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene()
    {
        // disposes all objects and the SPLASH SCENE itself to free memory

        // first part
        tambaiGamesSprite.clearEntityModifiers();
        tambaiGamesSprite.clearUpdateHandlers();
        tambaiGamesSprite.detachSelf();
        tambaiGamesSprite.dispose();

        // second part
        loadingText.clearEntityModifiers();
        loadingText.clearUpdateHandlers();
        loadingText.detachSelf();
        loadingText.dispose();

        splashLoadingBgSprite.clearEntityModifiers();
        splashLoadingBgSprite.clearUpdateHandlers();
        splashLoadingBgSprite.detachSelf();
        splashLoadingBgSprite.dispose();

        // this scene
        this.clearEntityModifiers();
        this.clearUpdateHandlers();
        this.detachSelf();
        this.dispose();

        System.gc();
    }

    //--------------------------------------------
    // SCENE LOGIC
    //--------------------------------------------
    //
    // show black scene (1 sec) => fade-in tambai games logo (1 sec)
    // => show tambai games logo (2 sec) => fade-out tambai games logo (0.5 sec)
    // => wait (0.5 sec) => show splash loading (2 sec) => load menu resources (?)
    //
    //--------------------------------------------

    // shows the tambai games logo with fade-in and fade-out modifiers
    private void showTambaiGamesLogo()
    {
        // creating the tambai games logo sprite
        tambaiGamesSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.tambaiGamesLogoRegion, vbom);

        // fade-in and fade-out modifiers
        final FadeInModifier fadeInMod = new FadeInModifier(1f);
        final FadeOutModifier fadeOutMod = new FadeOutModifier(0.5f);

        // timer-in of the first part of the SPLASH SCENE
        TimerHandler tambaiGamesLogoTimerIn = new TimerHandler(1f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                unregisterUpdateHandler(pTimerHandler);

                // show with fade-in the tambai games logo
                tambaiGamesSprite.registerEntityModifier(fadeInMod);
                attachChild(tambaiGamesSprite);
            }
        });

        this.registerUpdateHandler(tambaiGamesLogoTimerIn);

        // timer-out of the first part of the SPLASH SCENE
        TimerHandler tambaiGamesLogoTimerOut = new TimerHandler(4f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                unregisterUpdateHandler(pTimerHandler);

                // fade-out the tambai games logo
                tambaiGamesSprite.registerEntityModifier(fadeOutMod);
            }
        });

        this.registerUpdateHandler(tambaiGamesLogoTimerOut);
    }

    // shows the splash loading instantly with loading text and animation
    private void showSplashLoading()
    {
        // creating the splash loading background sprite with dither enabled
        splashLoadingBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.splashLoadingBgRegion, vbom)
        {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera)
            {
                pGLState.enableDither();
                super.preDraw(pGLState, pCamera);
            }

            @Override
            protected void postDraw(GLState pGLState, Camera pCamera)
            {
                pGLState.disableDither();
                super.postDraw(pGLState, pCamera);
            }
        };

        // creates a sprite background object with the splash loading background as sprite
        final SpriteBackground splashSceneSpriteBg = new SpriteBackground(splashLoadingBgSprite);

        // timer-in of the second part of the SPLASH SCENE
        TimerHandler splashLoadingTimerIn = new TimerHandler(5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                unregisterUpdateHandler(pTimerHandler);

                // sets the background to the splash loading background sprite we created
                setBackground(splashSceneSpriteBg);
            }
        });

        this.registerUpdateHandler(splashLoadingTimerIn);

        // timer-in of the loading text of the SPLASH SCENE
        TimerHandler loadingTextTimerIn = new TimerHandler(5.5f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                unregisterUpdateHandler(pTimerHandler);

                // shows the text loading
                loadingText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.35f,
                        resourceManager.splashLoadingFont, "LOADING", vbom);
                attachChild(loadingText);
            }
        });

        this.registerUpdateHandler(loadingTextTimerIn);

        // timer-out of the second part of the SPLASH SCENE
        TimerHandler splashLoadingTimerOut = new TimerHandler(7f, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                unregisterUpdateHandler(pTimerHandler);

                // load menu resources and create menu scene
                SceneManager.getInstance().createMenuScene();
            }
        });

        this.registerUpdateHandler(splashLoadingTimerOut);
    }
}
