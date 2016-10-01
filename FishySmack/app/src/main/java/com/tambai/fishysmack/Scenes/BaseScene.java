package com.tambai.fishysmack.Scenes;

import android.app.Activity;

import com.tambai.fishysmack.Managers.ResourceManager;
import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

public abstract class BaseScene extends Scene
{
    //--------------------------------------------
    // VARIABLES
    //--------------------------------------------

    protected ResourceManager resourceManager;
    protected Engine engine;
    protected Activity activity;
    protected Camera camera;
    protected VertexBufferObjectManager vbom;

    //--------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------

    public BaseScene()
    {
        this.resourceManager = ResourceManager.getInstance();
        this.engine = resourceManager.engine;
        this.activity = resourceManager.activity;
        this.camera = resourceManager.camera;
        this.vbom = resourceManager.vbom;
    }

    //--------------------------------------------
    // ABSTRACTION
    //--------------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneManager.SceneType getSceneType();

    public abstract void disposeScene();

}
