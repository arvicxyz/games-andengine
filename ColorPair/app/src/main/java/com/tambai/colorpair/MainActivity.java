package com.tambai.colorpair;

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
import org.andengine.entity.scene.Scene;
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
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.ArrayList;

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
    private Text quitText;
    private Text menuText;
    private static Text timeText;
    private static Text scoreText;
    private static Text gameOverText;

    // graphics
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    private TiledTextureRegion cardRegion;

    // music

    // sounds

    // in-game
    private static final int rowCardNum = 6;
    private static final int columnCardNum = 6;
    private static final int cardHorizontalSpacing = 60;
    private static final int cardVerticalSpacing = 75;

    public static final int pointsForMatch = 100;
    public static final int pointsForMiss = -5;
    public static Card firstCard;
    public static Card secondCard;
    public static int cardsLeft = 0;
    public static int score = 0;
    public static int time = 0;
    public static boolean isFirstClickDone = false;
    public static boolean isGameStart = false;
    public static boolean isGameOver = false;
    public static TimerHandler timerHandler;

    //-----------------------------------------
    // UPDATE HANDLER
    //-----------------------------------------

    // updates the game every time the scene refreshes
    private void gameUpdateHandler()
    {
        IUpdateHandler gameUpdate = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                if (isGameStart)
                {
                    startTimer();
                    isGameStart = false;
                }

                if (isGameOver)
                {
                    stopTimer();
                    showGameOver();
                    isGameOver = false;
                }
            }

            @Override
            public void reset()
            {
            }
        };

        scene.registerUpdateHandler(gameUpdate);
    }

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
        scene.getBackground().setColor(Color.BLACK);

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
                this.getAssets(), "game_font.otf", camera.getHeight() * 0.1f, true, Color.WHITE_ABGR_PACKED_INT,
                0, Color.WHITE_ABGR_PACKED_INT);
        menuFont = FontFactory.createStrokeFromAsset(this.getFontManager(), menuTextFontTexture,
                this.getAssets(), "game_font.otf", camera.getHeight() * 0.08f, true, Color.WHITE_ABGR_PACKED_INT,
                0, Color.WHITE_ABGR_PACKED_INT);

        titleFont.load();
        menuFont.load();
    }

    // loads graphic resources
    private void loadGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        gameTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 240, 256, TextureOptions.BILINEAR);

        cardRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, this,
                "card.png", 5, 4);

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
                "Match the Color", this.getVertexBufferObjectManager());
        mainMenuLayer.attachChild(titleText);

        // creating, setting color to RED, registering touch area and attaching play text
        playText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.5f, menuFont,
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

        playText.setColor(Color.RED);
        scene.registerTouchArea(playText);
        mainMenuLayer.attachChild(playText);

        // creating, setting color to YELLOW, registering touch area and attaching quit text
        quitText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.4f, menuFont,
                "quit", this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                // quit function
                if (pSceneTouchEvent.isActionDown())
                {
                    this.setScale(1.2f);
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    System.exit(0);
                    this.setScale(1);
                }
                else
                {
                    this.setScale(1);
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        quitText.setColor(Color.BLUE);
        scene.registerTouchArea(quitText);
        mainMenuLayer.attachChild(quitText);
    }

    private void loadGameScene()
    {
        gameUpdateHandler();

        // unregistering touch areas
        if (menuText != null)
        {
            scene.registerTouchArea(menuText);
        }

        scene.unregisterTouchArea(playText);
        scene.unregisterTouchArea(quitText);

        // calculating the offsets where the cards will be placed
        int cardContainerX = (int) (((cardRegion.getWidth() + (camera.getWidth() * 0.02083f)) * rowCardNum)
                - (cardVerticalSpacing - cardRegion.getWidth()));
        int screenSides = (int) ((camera.getWidth() - cardContainerX) / 2);

        int cardOffsetX = (int) (screenSides + (cardRegion.getWidth() / 2));
        int cardOffsetY = (int) (camera.getHeight() * 0.2f);

        // storing cards in an array list
        final ArrayList<Card> cardList = new ArrayList<>();

        for (int i = 1; i < ((rowCardNum * columnCardNum) / 2) + 1; i++)
        {
            Card firstCard = new Card(i, 0, 0, cardRegion.getWidth() + (camera.getWidth() * 0.02083f),
                    cardRegion.getHeight() + (camera.getHeight() * 0.0125f),
                    cardRegion, this.getVertexBufferObjectManager());
            cardList.add(firstCard);
            Card secondCard = new Card(i, 0, 0, cardRegion.getWidth() + (camera.getWidth() * 0.02083f),
                    cardRegion.getHeight() + (camera.getHeight() * 0.0125f),
                    cardRegion, this.getVertexBufferObjectManager());
            cardList.add(secondCard);
        }

        // creating the front of the cards
        for (int j = 0; j < columnCardNum; j++)
        {
            for (int k = 0; k < rowCardNum; k++)
            {
                int rndNum = (int) Math.floor(Math.random() * cardList.size());
                Card card = cardList.get(rndNum);
                card.detachSelf();
                card.setCurrentTileIndex(0);
                card.setX((j * cardHorizontalSpacing) + cardOffsetX);
                card.setY((k * cardVerticalSpacing) + cardOffsetY);
                scene.registerTouchArea(card);
                gameSceneLayer.attachChild(card);
                int index = cardList.indexOf(card);
                cardList.remove(index);
                cardsLeft++;
            }
        }

        // creating, setting color to WHITE, registering touch area and attaching score text
        scoreText = new Text(camera.getWidth() * 0.75f, camera.getHeight() * 0.95f, menuFont,
                "score: 0123456789", this.getVertexBufferObjectManager());
        scoreText.setText("score: 0");
        scoreText.setColor(Color.WHITE);
        scoreText.setScale(0.5f);
        gameSceneLayer.attachChild(scoreText);

        // creating, setting color to WHITE, registering touch area and attaching time text
        timeText = new Text(camera.getWidth() * 0.25f, camera.getHeight() * 0.95f, menuFont,
                "time: 0123456789 sec", this.getVertexBufferObjectManager());
        timeText.setText("time: 0 sec");
        timeText.setColor(Color.WHITE);
        timeText.setScale(0.5f);
        gameSceneLayer.attachChild(timeText);

        // creating, setting color to WHITE, registering touch area and attaching menu text
        menuText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.05f, menuFont,
                "quit", this.getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                // options function
                if (pSceneTouchEvent.isActionDown())
                {
                    this.setScale(1.2f);
                }
                else if (pSceneTouchEvent.isActionUp())
                {
                    firstCard = null;
                    secondCard = null;
                    cardsLeft = 0;
                    isFirstClickDone = false;
                    isGameStart = false;
                    isGameOver = false;
                    score = 0;
                    time = 0;
                    resetScore();
                    restartTimer();
                    scoreText.detachSelf();
                    timeText.detachSelf();
                    if (gameOverText != null)
                    {
                        gameOverText.detachSelf();
                    }
                    gameSceneLayer.setVisible(false);
                    mainMenuLayer.setVisible(true);
                    scene.unregisterTouchArea(this);
                    scene.registerTouchArea(playText);
                    scene.registerTouchArea(quitText);
                    this.setScale(1);
                    System.exit(0);
                }
                else
                {
                    this.setScale(1);
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        menuText.setColor(Color.BLUE);
        scene.registerTouchArea(menuText);
        gameSceneLayer.attachChild(menuText);
    }

    //-----------------------------------------
    // GAME FUNCTION METHODS
    //-----------------------------------------

    // resets score
    public void resetScore()
    {
        score = 0;
        scoreText.setText("score: " + String.valueOf(score));
    }

    // adds score
    public static void addScore()
    {
        score += pointsForMatch;
        cardsLeft -= 2;
        scoreText.setText("score: " + String.valueOf(score));
    }

    // subtracts score
    public static void subtractScore()
    {
        score += pointsForMiss;
        scoreText.setText("score: " + String.valueOf(score));
    }

    // resets timer
    public void restartTimer()
    {
        timeText.unregisterUpdateHandler(timerHandler);
        timeText.clearUpdateHandlers();
        time = 0;
        timeText.setText("time: " + String.valueOf(time) + " sec");
    }

    // starts timer
    public static void startTimer()
    {
        timerHandler = new TimerHandler(1f, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                time += 1;
                timeText.setText("time: " + String.valueOf(time) + " sec");
            }
        });
        timeText.registerUpdateHandler(timerHandler);
    }

    // stops timer
    public static void stopTimer()
    {
        timeText.unregisterUpdateHandler(timerHandler);
        timeText.clearUpdateHandlers();
    }

    // shows game over screen
    public void showGameOver()
    {
        String gameOverString = "congratulations!\nscore: " + score + "\ntime: " + time + " sec";
        gameOverText = new Text(camera.getWidth() / 2, camera.getHeight() / 2, menuFont,
                gameOverString, this.getVertexBufferObjectManager());
        gameOverText.setColor(Color.YELLOW);
        gameSceneLayer.attachChild(gameOverText);
    }
}
