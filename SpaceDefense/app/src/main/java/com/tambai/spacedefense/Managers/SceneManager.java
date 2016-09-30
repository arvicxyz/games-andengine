package com.tambai.spacedefense.Managers;

import com.tambai.spacedefense.Scenes.BaseScene;
import com.tambai.spacedefense.Scenes.GameScene;
import com.tambai.spacedefense.Scenes.MenuScene;
import com.tambai.spacedefense.Scenes.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager
{
    //---------------------------------------
    // SCENES
    //---------------------------------------

    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;

    //---------------------------------------
    // VARIABLES
    //---------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    private Engine engine = ResourceManager.getInstance().engine;
    private BaseScene currentScene;
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME
    }

    //---------------------------------------
    // SCENE MANAGER
    //---------------------------------------

    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }

    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            default:
                break;
        }
    }

    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourceManager.getInstance().loadSplashResources();
        splashScene = new SplashScene();
        currentScene = splashScene;

        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    public void disposeSplashScene()
    {
        ResourceManager.getInstance().unloadSplashResources();
        splashScene.disposeScene();
        splashScene = null;
    }

    public void createMenuScene()
    {
        ResourceManager.getInstance().loadMenuResources();
        menuScene = new MenuScene();
        setScene(menuScene);
        disposeSplashScene();
    }

    public void loadGameScene()
    {
        ResourceManager.getInstance().loadGameResources();
        gameScene = new GameScene();
        setScene(gameScene);
    }

    public void loadMenuScene()
    {
        ResourceManager.getInstance().unloadGameTextures();
        gameScene.disposeScene();
        setScene(menuScene);
    }

    //---------------------------------------
    // STATIC METHODS
    //---------------------------------------

    public BaseScene getCurrentScene()
    {
        return currentScene;
    }

    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }

    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
}
