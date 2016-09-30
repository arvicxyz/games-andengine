package com.tambai.spacedefense;

import android.view.KeyEvent;

import com.tambai.spacedefense.Managers.ResourceManager;
import com.tambai.spacedefense.Managers.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

public class MainActivity extends BaseGameActivity
{
    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private Camera gameCamera;

    @Override
    public EngineOptions onCreateEngineOptions()
    {
        gameCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        RatioResolutionPolicy gameCanvas = new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions gameEngineOptions = new EngineOptions(true,
                ScreenOrientation.LANDSCAPE_SENSOR, gameCanvas, gameCamera);

        gameEngineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        gameEngineOptions.getAudioOptions().setNeedsSound(true);

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
        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
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
}
