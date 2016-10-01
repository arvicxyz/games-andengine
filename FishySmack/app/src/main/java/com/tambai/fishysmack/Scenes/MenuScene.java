package com.tambai.fishysmack.Scenes;

import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

public class MenuScene extends BaseScene
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    // background
    private Sprite mainMenuBgSprite;

    // logo
    private Sprite fishyTaleSprite;

    // button
    private ButtonSprite playButtonSprite;

    // designs
    // TODO

    //--------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------

    public MenuScene()
    {
        createScene();
    }

    //--------------------------------------------
    // MENU SCENE
    //--------------------------------------------

    @Override
    public void createScene()
    {
        // sets the background of the MENU SCENE
        mainMenuBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.mainMenuBgRegion, vbom);

        // creates a sprite background object with the main menu background as sprite
        SpriteBackground menuSceneSpriteBg = new SpriteBackground(mainMenuBgSprite);
        setBackground(menuSceneSpriteBg);

        // calls the three methods responsible for displaying MENU SCENE objects
        createMenuSceneDesigns();
        createMenuSceneButton();
        createSmashTheBombsLogo();

        // calls the method for ignoring unnecessary updates done in the MENU SCENE
        ignoreMenuSceneUpdates();
    }

    @Override
    public void onBackKeyPressed()
    {
        // when on the MENU SCENE and pressed back key, a dialog pops up to confirm quit
        System.exit(0);
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        // returns the type of this scene, SCENE_MENU
        return SceneManager.SceneType.SCENE_MENU;
    }

    @Override
    public void disposeScene()
    {
        // the MENU SCENE is never detached and disposed
    }

    //--------------------------------------------
    // MENU SCENE UPDATE HANDLER
    //--------------------------------------------

    // ignores the MENU SCENE updates
    private void ignoreMenuSceneUpdates()
    {
        fishyTaleSprite.setIgnoreUpdate(true);
        playButtonSprite.setIgnoreUpdate(true);
    }

    //--------------------------------------------
    // SCENE LOGIC
    //--------------------------------------------

    // creates the smash the bombs logo of the MENU SCENE
    private void createSmashTheBombsLogo()
    {
        // creating and attaching the fishy tale logo sprite
        fishyTaleSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() * 0.75f,
                resourceManager.fishyTaleLogoRegion.getWidth() * 0.85f,
                resourceManager.fishyTaleLogoRegion.getHeight() * 0.85f,
                resourceManager.fishyTaleLogoRegion, vbom);

        this.attachChild(fishyTaleSprite);
    }

    // creates all the button sprite of the MENU SCENE
    private void createMenuSceneButton()
    {
        // creating and registering touch areas of play button sprite
        playButtonSprite = new ButtonSprite(camera.getWidth() / 2, camera.getHeight() * 0.3f,
                resourceManager.playButtonRegion, vbom)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionDown())
                {
                    resourceManager.clickSound.play();
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // play button function
                            SceneManager.getInstance().createGameScene();
                        }
                    });
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        this.registerTouchArea(playButtonSprite);

        // attaching all the button sprite
        this.attachChild(playButtonSprite);
    }

    // creates all the design graphics of the MENU SCENE
    private void createMenuSceneDesigns()
    {
        // TODO
    }
}
