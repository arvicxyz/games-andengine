package com.tambai.tapmania.Scenes;

import com.tambai.tapmania.Managers.SceneManager;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.util.adt.color.Color;

public class MenuScreenScene extends BaseScene implements IOnMenuItemClickListener
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private MenuScene menuChildScene;
    private final int MENU_PLAY = 0;

    //-----------------------------------------
    // MENU SCREEN SCENE
    //-----------------------------------------

    @Override
    public void createScene()
    {
        this.getBackground().setColor(Color.BLUE);

        createMenuChildScene();
    }

    @Override
    public void onBackKeyPressed()
    {
        System.exit(0);
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        return SceneManager.SceneType.SCENE_MENU;
    }

    @Override
    public void disposeScene()
    {
        menuChildScene.detachSelf();
        menuChildScene.dispose();
    }

    private void createMenuChildScene()
    {
        menuChildScene = new MenuScene(camera);
        menuChildScene.setPosition(camera.getWidth() / 2, camera.getHeight() / 2);

        final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY,
                resourceManager.playButtonRegion, vbom), 1.1f, 0.9f);
        menuChildScene.addMenuItem(playMenuItem);

        menuChildScene.buildAnimations();
        menuChildScene.setBackgroundEnabled(false);
        playMenuItem.setPosition(0, 0);
        menuChildScene.setOnMenuItemClickListener(this);

        this.setChildScene(menuChildScene);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
    {
        switch (pMenuItem.getID())
        {
            case MENU_PLAY:
                SceneManager.getInstance().loadGameScene();
                return true;
            default:
                return false;
        }
    }
}
