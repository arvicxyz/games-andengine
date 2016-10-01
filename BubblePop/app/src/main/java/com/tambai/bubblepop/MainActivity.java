package com.tambai.bubblepop;

import android.content.Context;
import android.view.KeyEvent;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends BaseGameActivity
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 800;
    private Camera camera;

    private Scene scene;

    // layers
    private Entity mainMenuLayer;
    private Entity gameSceneLayer;

    // fonts
    private Font titleFont;
    private Font menuFont;

    private Text titleText;
    private Text playText;
    private Text gameOverText;
    private static Text scoreText;

    // graphics
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    private ITextureRegion bubbleRegion;
    private Sprite bubbleSprite;

    // music

    // sounds

    // in-game
    private static final int pointsForHit = 1;
    private static int score = 0;
    private static int best;
    private LinkedList<Sprite> bubblesLL;
    private LinkedList<Sprite> bubblesToBeAdded;
    private IUpdateHandler detect;
    private TimerHandler bubbleFloatTimerHandler;
    private boolean isGameOver = false;

    //-----------------------------------------
    // GAME ACTIVITY METHODS
    //-----------------------------------------

    @Override
    public EngineOptions onCreateEngineOptions()
    {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        RatioResolutionPolicy canvas = new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, canvas, camera);

        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getAudioOptions().setNeedsSound(true);
        engineOptions.getAudioOptions().setNeedsMusic(true);

        return engineOptions;
    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
        return new FixedStepEngine(pEngineOptions, 60);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
    {
        // loading fonts, graphics, music and sounds
        loadFonts();
        loadGraphics();
        loadMusic();
        loadSounds();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
    {
        // creating and setting the game scene
        scene = new Scene();
        scene.getBackground().setColor(Color.WHITE);

        // creating and attaching layers
        mainMenuLayer = new Entity();
        gameSceneLayer = new Entity();

        scene.attachChild(mainMenuLayer);
        scene.attachChild(gameSceneLayer);

        // setting layer visibility
        mainMenuLayer.setVisible(true);
        gameSceneLayer.setVisible(false);

        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
    {
        loadMainMenu();
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (gameSceneLayer.isVisible() && keyCode == KeyEvent.KEYCODE_BACK)
        {
            System.exit(0);
        }
        else if (mainMenuLayer.isVisible() && keyCode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //-----------------------------------------
    // GAME LOGIC METHODS
    //-----------------------------------------

    // loads font resources
    private void loadFonts()
    {
        FontFactory.setAssetBasePath("fonts/");

        ITexture gameTitleFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        ITexture menuTextFontTexture = new BitmapTextureAtlas(this.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        titleFont = FontFactory.createStrokeFromAsset(this.getFontManager(), gameTitleFontTexture,
                this.getAssets(), "game_font.TTF", camera.getHeight() * 0.15f, true, Color.BLACK_ABGR_PACKED_INT,
                0, Color.BLACK_ABGR_PACKED_INT);
        menuFont = FontFactory.createStrokeFromAsset(this.getFontManager(), menuTextFontTexture,
                this.getAssets(), "game_font.TTF", camera.getHeight() * 0.1f, true, Color.RED_ABGR_PACKED_INT,
                0, Color.RED_ABGR_PACKED_INT);

        titleFont.load();
        menuFont.load();
    }

    // loads graphic resources
    private void loadGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        gameTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 240, 256, TextureOptions.BILINEAR);
        bubbleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, this,
                "bubble.png");

        try
        {
            gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
            gameTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    // loads music resources
    private void loadMusic()
    {
        MusicFactory.setAssetBasePath("mfx/");
    }

    // loads sound resources
    private void loadSounds()
    {
        SoundFactory.setAssetBasePath("sfx/");
    }

    private void loadMainMenu()
    {
        // creating and attaching title text
        titleText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.7f, titleFont,
                "Pop the\nBalloon", this.getVertexBufferObjectManager());
        mainMenuLayer.attachChild(titleText);

        // creating, setting color to RED, registering touch area and attaching play text
        playText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.3f, menuFont,
                "play", this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                // play function
                if (pSceneTouchEvent.isActionDown())
                {
                    this.setScale(1.2f);
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    loadGameScene();
                    mainMenuLayer.setVisible(false);
                    gameSceneLayer.setVisible(true);
                    this.setScale(1);
                }
                else
                {
                    this.setScale(1);
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        scene.registerTouchArea(playText);
        mainMenuLayer.attachChild(playText);
    }

    private void loadGameScene()
    {
        best = getBestScore();
        bubblesLL = new LinkedList<Sprite>();
        bubblesToBeAdded = new LinkedList<Sprite>();
        scene.unregisterTouchArea(playText);

        // creating, setting color to WHITE, registering touch area and attaching score text
        scoreText = new Text(camera.getWidth() * 0.5f, camera.getHeight() * 0.95f, menuFont,
                "score: 0123456789", this.getVertexBufferObjectManager());
        scoreText.setText("score: 0");
        scoreText.setScale(0.5f);
        gameSceneLayer.attachChild(scoreText);

        createSpriteSpawnTimeHandler();
    }

    //-----------------------------------------
    // GAME FUNCTION METHODS
    //-----------------------------------------

    private void addFloatingBalloons()
    {
        int minX = (int) (bubbleRegion.getWidth() / 2);
        int maxX = (int) (camera.getWidth() - bubbleRegion.getWidth() / 2);
        int y = (int) (-bubbleRegion.getHeight());
        int rangeX = maxX - minX;
        int x = (int) ((Math.random() * rangeX) + minX);

        bubbleSprite = new Sprite(x, y, bubbleRegion.deepCopy(), this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if (pSceneTouchEvent.isActionDown())
                {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            setVisible(false);
                            detachSelf();
                            addScore();
                        }
                    });
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        scene.registerTouchArea(bubbleSprite);
        gameSceneLayer.attachChild(bubbleSprite);

        int minDuration = 2;
        int maxDuration = 5;
        int rangeDuration = maxDuration - minDuration;
        int actualDuration = (int) ((Math.random() * rangeDuration) + minDuration);

        MoveYModifier mod = new MoveYModifier(actualDuration, bubbleSprite.getY(),
                camera.getHeight() + bubbleSprite.getHeight());
        bubbleSprite.registerEntityModifier(mod.deepCopy());
        bubblesToBeAdded.add(bubbleSprite);
    }

    private void createSpriteSpawnTimeHandler()
    {
        float bombsSpawnDelay = 1f;

        bubbleFloatTimerHandler = new TimerHandler(bombsSpawnDelay, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                if (!isGameOver)
                {
                    addFloatingBalloons();
                }
            }
        });

        mEngine.registerUpdateHandler(bubbleFloatTimerHandler);

        detect = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                final Iterator<Sprite> bubbles = bubblesLL.iterator();
                Sprite bubble;

                while (bubbles.hasNext())
                {
                    bubble = bubbles.next();

                    if (bubble.getY() >= camera.getHeight() + bubble.getHeight())
                    {
                        isGameOver = true;
                        scene.unregisterUpdateHandler(detect);
                        scene.unregisterUpdateHandler(bubbleFloatTimerHandler);
                        showGameOver();
                        removeSprite(bubble, bubbles);
                        break;
                    }
                }

                bubblesLL.addAll(bubblesToBeAdded);
                bubblesToBeAdded.clear();
            }

            @Override
            public void reset()
            {
            }
        };

        scene.registerUpdateHandler(detect);
    }

    private void removeSprite(final Sprite pSprite, Iterator<Sprite> pIterator)
    {
        mEngine.runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                pSprite.setVisible(false);
                pSprite.detachSelf();
                pSprite.dispose();
            }
        });

        pIterator.remove();
    }

    // adds score
    private void addScore()
    {
        if (!isGameOver)
        {
            score += pointsForHit;
            scoreText.setText("score: " + String.valueOf(score));
        }
    }

    // shows game over screen
    private void showGameOver()
    {
        String gameOverString;

        if (score > best)
        {
            best = score;
            setBestScore(best);
            gameOverString = "new best score!\nscore: " + score + "\nbest: " + best;
        }
        else
        {
            gameOverString = "nice game!\nscore: " + score + "\nbest: " + best;
        }

        gameOverText = new Text(camera.getWidth() / 2, camera.getHeight() / 2, menuFont,
                gameOverString, this.getVertexBufferObjectManager());
        gameOverText.setScale(0.5f);
        gameOverText.setColor(Color.BLACK);
        gameSceneLayer.attachChild(gameOverText);
    }

    private int getBestScore()
    {
        return this.getPreferences(Context.MODE_PRIVATE).getInt("bestScore", 0);
    }

    private void setBestScore(int best)
    {
        this.getPreferences(Context.MODE_PRIVATE).edit().putInt("bestScore", best).apply();
    }
}
