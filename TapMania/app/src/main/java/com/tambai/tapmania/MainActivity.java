package com.tambai.tapmania;

import android.view.KeyEvent;

import com.tambai.tapmania.Managers.ResourceManager;
import com.tambai.tapmania.Managers.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.CroppedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

public class MainActivity extends BaseGameActivity
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 800;
    private Camera gameCamera;

    //-----------------------------------------
    // GAME ACTIVITY
    //-----------------------------------------

    @Override
    public EngineOptions onCreateEngineOptions()
    {
        gameCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        CroppedResolutionPolicy gameCanvas = new CroppedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions gameEngineOptions = new EngineOptions(true,
                ScreenOrientation.PORTRAIT_FIXED, gameCanvas, gameCamera);

        gameEngineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

        return gameEngineOptions;
    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
    {
        ResourceManager.readyResourceManager(mEngine, this, gameCamera, getVertexBufferObjectManager());
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
    {
        SceneManager.getInstance().createMenuScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
    {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }

        return false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.exit(0);
    }
}
