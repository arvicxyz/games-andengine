package com.tambai.spacedefense.Scenes;

import com.tambai.spacedefense.Managers.SceneManager;

import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

public class MenuScene extends BaseScene
{
    private Text startGameText;

    @Override
    public void createScene()
    {
        this.getBackground().setColor(Color.WHITE);

        startGameText = new Text(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.menuFont, "Start Game", vbom)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionDown())
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            resourceManager.clickButton.play();
                            SceneManager.getInstance().loadGameScene();
                        }
                    });
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        this.registerTouchArea(startGameText);
        attachChild(startGameText);
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
        // None
    }
}
