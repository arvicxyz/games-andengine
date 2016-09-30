package com.tambai.tapmania.Managers;

import com.tambai.tapmania.Scenes.BaseScene;
import com.tambai.tapmania.Scenes.GameScreenScene;
import com.tambai.tapmania.Scenes.MenuScreenScene;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager
{
    //-----------------------------------------
    // SCENES
    //-----------------------------------------

    private BaseScene menuScreenScene;
    private BaseScene gameScreenScene;

    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private static final SceneManager INSTANCE = new SceneManager();

    private Engine engine = ResourceManager.getInstance().engine;
    private BaseScene currentScene;
    private SceneType currentSceneType = SceneType.SCENE_MENU;

    public enum SceneType
    {
        SCENE_MENU,
        SCENE_GAME
    }

    //-----------------------------------------
    // SCENE MANAGER
    //-----------------------------------------

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
            case SCENE_MENU:
                setScene(menuScreenScene);
                break;
            case SCENE_GAME:
                setScene(gameScreenScene);
                break;
            default:
                break;
        }
    }

    public void createMenuScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
        ResourceManager.getInstance().loadMenuResources();
        menuScreenScene = new MenuScreenScene();
        currentScene = menuScreenScene;

        pOnCreateSceneCallback.onCreateSceneFinished(menuScreenScene);
    }

    public void loadGameScene()
    {
        ResourceManager.getInstance().loadGameResources();
        gameScreenScene = new GameScreenScene();
        setScene(gameScreenScene);
    }

    public void loadMenuScene()
    {
        ResourceManager.getInstance().unloadGameResources();
        gameScreenScene.disposeScene();
        setScene(menuScreenScene);
    }

    //-----------------------------------------
    // STATIC METHODS
    //-----------------------------------------

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
