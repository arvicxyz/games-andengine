package com.tambai.fishysmack.Scenes;

import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

public class LoadingScene extends BaseScene
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    // background
    private Sprite loadingScreenBgSprite;

    // loading text
    private Text loadingText;

    //--------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------

    public LoadingScene()
    {
        createScene();
    }

    //--------------------------------------------
    // LOADING SCENE
    //--------------------------------------------

    @Override
    public void createScene()
    {
        // sets the background of the LOADING SCENE
        loadingScreenBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.loadingScreenBgRegion, vbom);

        // creates a sprite background object with the game screen background as sprite
        SpriteBackground loadingSceneSpriteBg = new SpriteBackground(loadingScreenBgSprite);
        setBackground(loadingSceneSpriteBg);

        // creates a loading text and show it
        loadingText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.35f,
                resourceManager.loadingTextFont, "LOADING", vbom);
        attachChild(loadingText);
    }

    @Override
    public void onBackKeyPressed()
    {
        // when on the LOADING SCENE, you cannot use back key to quit
        return;
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        // returns the type of this scene, SCENE_LOADING
        return SceneManager.SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene()
    {
        // the LOADING SCENE is never detached and disposed
    }
}
