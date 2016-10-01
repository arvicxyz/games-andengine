package com.tambai.fishysmack.Managers;

import com.tambai.fishysmack.Scenes.BaseScene;
import com.tambai.fishysmack.Scenes.GameScene;
import com.tambai.fishysmack.Scenes.LoadingScene;
import com.tambai.fishysmack.Scenes.MenuScene;
import com.tambai.fishysmack.Scenes.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

/*
 * The SceneManager is the class that manages all the scenes.
 * Moving from one scene to another is the main purpose of
 * this class. It makes the navigation between scenes fast
 * and easy. This class can be called in every part of the
 * program to get the current scene and current SceneType
 * and set it on your choice. By AMIR FAHD HADJI USOP
 */

public class SceneManager
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    private Engine engine = ResourceManager.getInstance().engine;
    private BaseScene currentScene;
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_LOADING,
        SCENE_GAME
    }

    //--------------------------------------------
    // SCENES
    //--------------------------------------------

    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene loadingScene;
    private BaseScene gameScene;

    //--------------------------------------------
    // SCENE MANAGER
    //--------------------------------------------

    // sets the scene to the desired scene parameter
    public void setScene(BaseScene pScene)
    {
        this.engine.setScene(pScene);
        this.currentScene = pScene;
        this.currentSceneType = pScene.getSceneType();
    }

    // sets the scene to the desired SceneType parameter
    public void setScene(SceneType pSceneType)
    {
        switch (pSceneType)
        {
            case SCENE_SPLASH:
                setScene(this.splashScene);
                break;
            case SCENE_MENU:
                setScene(this.menuScene);
                break;
            case SCENE_LOADING:
                setScene(this.loadingScene);
                break;
            case SCENE_GAME:
                setScene(this.gameScene);
                break;
            default:
                break;
        }
    }

    // creates the SPLASH SCENE
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourceManager.getInstance().loadSplashResources();
        this.splashScene = new SplashScene();
        this.currentScene = this.splashScene;

        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);
    }

    // disposes the SPLASH SCENE
    public void disposeSplashScene()
    {
        ResourceManager.getInstance().unloadSplashResources();
        this.splashScene.disposeScene();
        this.splashScene = null;
    }

    // creates the MENU SCENE
    public void createMenuScene()
    {
        ResourceManager.getInstance().loadMenuResources();

        this.menuScene = new MenuScene();
        this.loadingScene = new LoadingScene();

        setScene(this.menuScene);
        disposeSplashScene();
    }

    // creates the GAME SCENE
    public void createGameScene()
    {
        setScene(this.loadingScene);
        ResourceManager.getInstance().unloadMenuTextures();

        this.engine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback()
        {
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                SceneManager.this.engine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadGameResources();
                SceneManager.this.gameScene = new GameScene();
                setScene(SceneManager.this.gameScene);
            }
        }));
    }

    // loads the MENU SCENE
    public void loadMenuScene()
    {
        setScene(this.loadingScene);
        ResourceManager.getInstance().unloadGameResources();
        this.gameScene.disposeScene();
        this.gameScene = null;

        this.engine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback()
        {
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                SceneManager.this.engine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadMenuTextures();
                setScene(SceneManager.this.menuScene);
            }
        }));
    }

    //--------------------------------------------
    // GETTERS AND SETTERS
    //--------------------------------------------

    // gets the current scene displayed
    public BaseScene getCurrentScene()
    {
        return this.currentScene;
    }

    // gets the current SceneType displayed
    public SceneType getCurrentSceneType()
    {
        return this.currentSceneType;
    }

    // gets an instance of the scene manager
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
}
