package com.tambai.fishysmack;

import android.view.KeyEvent;

import com.tambai.fishysmack.Managers.ResourceManager;
import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

/*
 * The FishyTaleActivity is the main class in the game and is
 * is responsible for managing all other classes. In this class
 * we can create the engine and its engine options. Game options
 * like camera, camera width, camera height, resolution policy,
 * screen orientation, wake lock options, sound and music needs
 * are all defined in this class. By AMIR FAHD HADJI USOP
 */

public class MainActivity extends BaseGameActivity
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 800;
    private Camera gameCamera;

    //--------------------------------------------
    // FISHY TALE ACTIVITY
    //--------------------------------------------

    // creates the engine's options including the screen orientation, resolution policy and camera
    @Override
    public EngineOptions onCreateEngineOptions()
    {
        this.gameCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        RatioResolutionPolicy gameCanvas = new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions gameEngineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, gameCanvas, this.gameCamera);

        gameEngineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        gameEngineOptions.getAudioOptions().setNeedsSound(true);

        return gameEngineOptions;
    }

    // creates a Fixed Step Engine with the created engine options and a standard 60 frames per second
    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
        return new FixedStepEngine(pEngineOptions, 60);
    }

    // creates the resources of the game and calls the Resource Manager to take over this function
    @Override
    public void onCreateResources(IGameInterface.OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
    {
        ResourceManager.readyResourceManager(mEngine, this, this.gameCamera, getVertexBufferObjectManager());
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    // creates the scenes of the game and calls the Scene Manager to take over this function
    @Override
    public void onCreateScene(IGameInterface.OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
    {
        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
    }

    // populates the scene but is not used because we will do it on the onCreateScene methods
    @Override
    public void onPopulateScene(Scene pScene, IGameInterface.OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
    {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    // manages the function of built in phone buttons inside the game
    @Override
    public boolean onKeyDown(int pKeyCode, KeyEvent pEvent)
    {
        if (pKeyCode == KeyEvent.KEYCODE_BACK)
        {
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }

        return false;
    }

    // executes the action for when the game is destroyed
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.exit(0);
    }
}
