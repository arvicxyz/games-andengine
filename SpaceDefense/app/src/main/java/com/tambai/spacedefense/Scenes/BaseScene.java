package com.tambai.spacedefense.Scenes;

import android.app.Activity;

import com.tambai.spacedefense.Managers.ResourceManager;
import com.tambai.spacedefense.Managers.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BaseScene extends Scene
{
    //---------------------------------------
    // VARIABLES
    //---------------------------------------

    protected Engine engine;
    protected Activity activity;
    protected Camera camera;
    protected VertexBufferObjectManager vbom;
    protected ResourceManager resourceManager;

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------

    public BaseScene()
    {
        this.resourceManager = ResourceManager.getInstance();
        this.engine = resourceManager.engine;
        this.activity = resourceManager.activity;
        this.camera = resourceManager.camera;
        this.vbom = resourceManager.vbom;

        createScene();
    }

    //---------------------------------------
    // ABSTRACTION
    //---------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneManager.SceneType getSceneType();

    public abstract void disposeScene();

}
