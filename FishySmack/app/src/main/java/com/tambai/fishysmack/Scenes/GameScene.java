package com.tambai.fishysmack.Scenes;

import android.content.Context;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tambai.fishysmack.Managers.SceneManager;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

public class GameScene extends BaseScene implements IOnSceneTouchListener
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    // background
    private Sprite gameScreenBgSprite;

    // HUD
    private HUD gameHud;
    private Text scoreText;

    // objects
    private Sprite heroSprite;
    private Sprite enemySprite;
    private Sprite groundSprite;
    private Sprite goldCoinSprite;

    // game over
    private Sprite gameOverSprite;
    private Text resultText;
    private ButtonSprite playAgainButtonSprite;

    // in-game
    private IUpdateHandler gameSceneUpdater;

    private IUpdateHandler physicsWorldUpdate;
    private PhysicsWorld gamePhysicsWorld;
    private PhysicsConnector physicsConnector;
    private FixtureDef groundFixtureDef;
    private FixtureDef heroFixtureDef;
    private Body groundBody;
    private Body heroBody;
    private float vX = 0f;
    private float vY = 0f;

    private final int TO_LEFT = 0;
    private final int TO_RIGHT = 1;
    private int direction;
    private float xTo;

    private LinkedList<Sprite> goldCoinSpawner;
    private LinkedList<Sprite> goldCoinDetector;
    private TimerHandler goldCoinSpawnTimerHandler;
    private IUpdateHandler goldCoinDetectUpdateHandler;
    private float goldCoinSpawnDelay = 3f;
    private int goldCoinX;
    private int goldCoinY;

    private LinkedList<Sprite> obstacleSpawner;
    private LinkedList<Sprite> obstacleDetector;
    private TimerHandler obstacleSpawnTimerHandler;
    private IUpdateHandler obstacleDetectUpdateHandler;
    private float obstacleSpawnDelay = 5f;

    // game variables
    private int best;
    private int score;
    private int tapCount;

    private boolean isGameStart;
    private boolean isPlaying;
    private boolean isGameOver;

    //--------------------------------------------
    // CONSTRUCTOR
    //--------------------------------------------

    public GameScene()
    {
        createScene();
    }

    //--------------------------------------------
    // GAME SCENE
    //--------------------------------------------

    @Override
    public void createScene()
    {
        // initialization of variables
        best = getBestScore();
        score = 0;
        tapCount = 0;
        isGameStart = false;
        isPlaying = true;
        isGameOver = false;

        goldCoinSpawner = new LinkedList<Sprite>();
        goldCoinDetector = new LinkedList<Sprite>();

        obstacleSpawner = new LinkedList<Sprite>();
        obstacleDetector = new LinkedList<Sprite>();

        // sets the background of the GAME SCENE
        gameScreenBgSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.gameScreenBgRegion, vbom);

        // creates a sprite background object with the game screen background as sprite
        SpriteBackground gameSceneSpriteBg = new SpriteBackground(gameScreenBgSprite);
        setBackground(gameSceneSpriteBg);

        // calls the three methods responsible for displaying GAME SCENE objects
        createGameSceneHud();
        createGameSceneObjects();

        // calls the method for ignoring unnecessary updates done in the GAME SCENE
        ignoreGameSceneUpdates();

        // calls the GAME SCENE updater
        createGameSceneUpdater();

        // sets the game scene touch listener to the implemented interface
        this.setOnSceneTouchListener(this);
    }

    @Override
    public void onBackKeyPressed()
    {
        // when on the GAME SCENE and pressed back key, a dialog pops up to ask quit game or back to main menu
        SceneManager.getInstance().loadMenuScene();
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        // returns the type of this scene, SCENE_GAME
        return SceneManager.SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
        // disposes all objects and the GAME SCENE itself to free memory

        // background
        gameScreenBgSprite.clearEntityModifiers();
        gameScreenBgSprite.clearUpdateHandlers();
        gameScreenBgSprite.detachSelf();
        gameScreenBgSprite.dispose();

        // HUD
        camera.setHUD(null);
        gameHud = null;

        // this scene
        this.clearEntityModifiers();
        this.clearUpdateHandlers();
        this.detachSelf();
        this.dispose();
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {
        if (isGameStart && !isGameOver)
        {
            if (pSceneTouchEvent.isActionDown())
            {
                tapCount++;

                if (tapCount == 1)
                {
                    heroSprite.setRotation(-15f);
                    createPhysics();
                    this.registerUpdateHandler(goldCoinSpawnTimerHandler);
                    this.registerUpdateHandler(goldCoinDetectUpdateHandler);
                    this.registerUpdateHandler(obstacleSpawnTimerHandler);
                    this.registerUpdateHandler(obstacleDetectUpdateHandler);
                }

                if (heroSprite.getY() < camera.getHeight())
                {
                    if (pSceneTouchEvent.getX() > camera.getWidth() / 2)
                    {
                        direction = TO_RIGHT;
                    }
                    else
                    {
                        direction = TO_LEFT;
                    }

                    engine.runOnUpdateThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            swimFishy(heroSprite, direction);
                        }
                    });

                    return true;
                }
            }
            else if (pSceneTouchEvent.isActionUp())
            {
                if (isPlaying)
                {
                    heroSprite.clearEntityModifiers();
                    RotationModifier rotMod = new RotationModifier(1f, heroSprite.getRotation(), 25f);
                    heroSprite.registerEntityModifier(rotMod);

                    return true;
                }
            }
        }

        return false;
    }

    private void createGameSceneUpdater()
    {
        gameSceneUpdater = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                if (!isGameStart)
                {
                    // starts the game
                    isGameStart = true;
                    GameScene.this.unregisterUpdateHandler(this);
                }
            }

            @Override
            public void reset()
            {
            }
        };

        this.registerUpdateHandler(gameSceneUpdater);
    }

    // ignores the GAME SCENE updates
    private void ignoreGameSceneUpdates()
    {
        gameHud.setIgnoreUpdate(true);
        scoreText.setIgnoreUpdate(true);
    }

    //--------------------------------------------
    // SCENE LOGIC
    //--------------------------------------------

    // creates the game HUD and all of its objects on the GAME SCENE
    private void createGameSceneHud()
    {
        // creating the game HUD
        gameHud = new HUD();

        // creating and attaching the coin text
        scoreText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.875f,
                resourceManager.gameScoreFont, "0123456789", vbom);
        scoreText.setText(String.valueOf(score).toString());
        gameHud.attachChild(scoreText);

        // setting the camera's HUD
        camera.setHUD(gameHud);
    }

    // creates the object spawner that spawns all game objects of GAME SCENE
    private void createGameSceneObjects()
    {
        heroSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() / 2,
                resourceManager.heroRegion, vbom);
        this.attachChild(heroSprite);

        groundSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() * 0.054167f,
                resourceManager.groundRegion, vbom);
        this.attachChild(groundSprite);

        createGoldCoinAndObstacleSpawnTimeHandler();
    }

    //-----------------------------------------
    // GAME FUNCTION METHODS
    //-----------------------------------------

    // calls all the game over functions
    private void gameOver()
    {
        isPlaying = false;

        if (score > best)
        {
            best = score;
            setBestScore(best);
        }

        // creating and attaching game over text
        gameOverSprite = new Sprite(camera.getWidth() / 2, camera.getHeight() * 0.8f,
                resourceManager.gameOverRegion, vbom);

        gameHud.attachChild(gameOverSprite);

        // creating and attaching result text
        resultText = new Text(camera.getWidth() / 2, camera.getHeight() * 0.55f,
                resourceManager.gameResultFont,
                "Score: " + String.valueOf(score) + "\nBest: " + String.valueOf(best), vbom);

        gameHud.attachChild(resultText);

        // calls game over functions
        this.unregisterUpdateHandler(goldCoinSpawnTimerHandler);
        this.unregisterUpdateHandler(obstacleSpawnTimerHandler);
        destroyEntity(scoreText);
        showPlayButton();
    }

    // creates the physics needed by the game to function
    private void createPhysics()
    {
        // creating physics world gravity and setting its contact listener
        gamePhysicsWorld = new PhysicsWorld(new Vector2(0, -SensorManager.GRAVITY_EARTH * 3), false);
        gamePhysicsWorld.setContactListener(createContactListener());

        // creating fixture definition and body of ground sprite
        groundFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        groundBody = PhysicsFactory.createBoxBody(gamePhysicsWorld,
                groundSprite, BodyDef.BodyType.StaticBody, groundFixtureDef);
        groundBody.setUserData("ground");
        groundSprite.setUserData(groundBody);

        // creating fixture definition and body of hero sprite
        heroFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        heroBody = PhysicsFactory.createCircleBody(gamePhysicsWorld,
                heroSprite, BodyDef.BodyType.DynamicBody, heroFixtureDef);
        heroBody.setUserData("hero");
        heroSprite.setUserData(heroBody);

        // creating physics connector and registering the physics world
        physicsConnector = new PhysicsConnector(heroSprite, heroBody, true, false);
        gamePhysicsWorld.registerPhysicsConnector(physicsConnector);

        // creating update handler for the physics world and updates it on every scene update
        physicsWorldUpdate = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                gamePhysicsWorld.onUpdate(pSecondsElapsed);
            }

            @Override
            public void reset()
            {
            }
        };

        // registering the update handler
        this.registerUpdateHandler(physicsWorldUpdate);
    }

    // destroys the physics created in the game
    private void destroyPhysics()
    {
        gamePhysicsWorld.setContactListener(null);
        gamePhysicsWorld.unregisterPhysicsConnector(physicsConnector);
        gamePhysicsWorld.clearPhysicsConnectors();
        this.unregisterUpdateHandler(physicsWorldUpdate);
        gamePhysicsWorld = null;
        groundFixtureDef = null;
        groundBody = null;
        heroFixtureDef = null;
        heroBody = null;
        physicsConnector = null;
    }

    // makes the hero sprite swim
    private void swimFishy(final Sprite pHero, int xDirection)
    {
        resourceManager.flapSound.play();
        xTo = heroSprite.getX();
        heroSprite.clearEntityModifiers();
        RotationModifier rotMod = new RotationModifier(0.3f, heroSprite.getRotation(), -45f);

        switch (xDirection)
        {
            case 0:
                xTo = heroSprite.getX() - camera.getWidth() * 0.5f;
                heroSprite.setFlippedHorizontal(true);
                break;
            case 1:
                xTo = heroSprite.getX() + camera.getWidth() * 0.5f;
                heroSprite.setFlippedHorizontal(false);
                break;
            default:
                xTo = heroSprite.getX();
                break;
        }

        MoveXModifier moveXMod = new MoveXModifier(0.5f, camera.getWidth() / 2, xTo);
        heroSprite.registerEntityModifier(rotMod);
        heroSprite.registerEntityModifier(moveXMod);
        final Body pHeroBody = (Body) pHero.getUserData();

        vY = camera.getHeight() * 0.0125f;
        final Vector2 velocity = Vector2Pool.obtain(vX, vY);
        pHeroBody.setLinearVelocity(velocity);
        Vector2Pool.recycle(velocity);
    }

    // contact listener that checks the collision of physics objects in the game like  the ground and maya bird
    private ContactListener createContactListener()
    {
        return new ContactListener()
        {
            // create the two bodies that will begin a contact with each one

            @Override
            public void beginContact(Contact contact)
            {
                final Fixture fixtureA = contact.getFixtureA();
                final Body bodyA = fixtureA.getBody();
                final String userDataA = (String) bodyA.getUserData();

                final Fixture fixtureB = contact.getFixtureB();
                final Body bodyB = fixtureB.getBody();
                final String userDataB = (String) bodyB.getUserData();

                if (("hero".equals(userDataA)) && ("ground".equals(userDataB))
                        || ("ground".equals(userDataA)) && ("hero".equals(userDataB)))
                {
                    if (!isGameOver)
                    {
                        isGameOver = true;
                        resourceManager.smackSound.play();

                        if (isGameOver)
                        {
                            gameOver();
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {
            }
        };
    }

    // shows the play button during score preview on game over
    private void showPlayButton()
    {
        // creating and attaching play button sprite
        playAgainButtonSprite = new ButtonSprite(camera.getWidth() / 2, camera.getHeight() * 0.3f,
                resourceManager.playAgainButtonRegion, vbom)
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
                            Iterator<Sprite> goldCoinList = goldCoinDetector.iterator();
                            Sprite goldCoin;

                            while (goldCoinList.hasNext())
                            {
                                goldCoin = goldCoinList.next();

                                // destroys gold coin and breaks process
                                destroyEntity(goldCoin);
                                goldCoinList.remove();

                                break;
                            }

                            destroyEntity(heroSprite);
                            destroyEntity(gameOverSprite);
                            destroyEntity(resultText);
                            destroyEntity(playAgainButtonSprite);
                            destroyPhysics();
                            createScene();
                        }
                    });
                }

                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        // registering the play button sprite
        this.registerTouchArea(playAgainButtonSprite);

        gameHud.attachChild(playAgainButtonSprite);
    }

    // adds score to the game
    private void addScore(int pScore)
    {
        score += pScore;
        scoreText.setText(String.valueOf(score));
    }

    // add falling gold coins
    private void addGoldCoins()
    {
        // setting the x and y of each spawned hero with random x
        goldCoinY = (int) (camera.getHeight() + resourceManager.goldCoinRegion.getHeight() / 2);
        int minX = (int) (resourceManager.goldCoinRegion.getWidth() / 2);
        int maxX = (int) (camera.getWidth() - resourceManager.goldCoinRegion.getWidth() / 2);
        int rangeX = maxX - minX;
        goldCoinX = (int) ((Math.random() * rangeX) + minX);

        // creating and attaching hero sprite
        goldCoinSprite = new Sprite(goldCoinX, goldCoinY,
                camera.getWidth() * 0.125f, camera.getHeight() * 0.075f,
                resourceManager.goldCoinRegion.deepCopy(), vbom);

        this.attachChild(goldCoinSprite);

        int actualDuration = 3;
        MoveYModifier mod = new MoveYModifier(actualDuration,
                goldCoinSprite.getY(), -goldCoinSprite.getHeight());
        goldCoinSprite.registerEntityModifier(mod.deepCopy());

        goldCoinSpawner.add(goldCoinSprite);
    }

    // adds an obstacle that moves from right to left
    private void addObstacle()
    {
        // setting the x and y of each spawned obstacle with random y
        int x = (int) (camera.getWidth() + resourceManager.enemyRegion.getWidth());
        int minY = (int) (groundSprite.getHeight() + heroSprite.getHeight());
        int maxY = (int) (camera.getHeight() - (scoreText.getX() + (scoreText.getWidth() / 2)));
        int rangeY = maxY - minY;
        int y = (int) (Math.random() * rangeY) + minY;

        // creating and attaching obstacle sprite
        enemySprite = new Sprite(x, y,
                resourceManager.enemyRegion.deepCopy(), vbom);
        enemySprite.setFlippedHorizontal(true);

        this.attachChild(enemySprite);

        int actualDuration = 5;
        MoveXModifier mod = new MoveXModifier(actualDuration, enemySprite.getX(), -enemySprite.getWidth());
        enemySprite.registerEntityModifier(mod.deepCopy());

        obstacleSpawner.add(enemySprite);
    }

    // creates the time handler that spawns flappy birds and the update handler that detects its collision and events
    private void createGoldCoinAndObstacleSpawnTimeHandler()
    {
        goldCoinSpawnTimerHandler = new TimerHandler(goldCoinSpawnDelay, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                addGoldCoins();
            }
        });

        obstacleSpawnTimerHandler = new TimerHandler(obstacleSpawnDelay, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                addObstacle();
            }
        });

        goldCoinDetectUpdateHandler = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                Iterator<Sprite> goldCoinList = goldCoinDetector.iterator();
                Sprite goldCoin;

                while (goldCoinList.hasNext())
                {
                    goldCoin = goldCoinList.next();

                    if (!isGameOver && heroSprite.collidesWith(goldCoin))
                    {
                        // adds 1 score for every gold coin
                        resourceManager.badingSound.play();
                        addScore(1);

                        // destroys gold coin and breaks process
                        destroyEntity(goldCoin);
                        goldCoinList.remove();

                        break;
                    }
                    else if (goldCoin.getY() < groundSprite.getHeight() + (goldCoin.getHeight() / 2))
                    {
                        destroyEntity(goldCoin);
                        goldCoinList.remove();

                        isGameOver = true;
                        resourceManager.smackSound.play();

                        if (isGameOver)
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    gameOver();
                                }
                            });
                        }

                        break;
                    }
                }

                goldCoinDetector.addAll(goldCoinSpawner);
                goldCoinSpawner.clear();
            }

            @Override
            public void reset()
            {
            }
        };

        obstacleDetectUpdateHandler = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                Iterator<Sprite> obstacleList = obstacleDetector.iterator();
                Sprite obstacle;

                while (obstacleList.hasNext())
                {
                    obstacle = obstacleList.next();

                    if (!isGameOver && heroSprite.collidesWith(obstacle))
                    {
                        isGameOver = true;
                        resourceManager.smackSound.play();

                        if (isGameOver)
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    gameOver();
                                }
                            });
                        }

                        break;
                    }
                    else if (obstacle.getX() <= -obstacle.getWidth())
                    {
                        // destroys obstacle and breaks process
                        destroyEntity(obstacle);
                        obstacleList.remove();
                    }
                }

                obstacleDetector.addAll(obstacleSpawner);
                obstacleSpawner.clear();
            }

            @Override
            public void reset()
            {
            }
        };
    }

    // destroys an entity
    private void destroyEntity(Entity pEntity)
    {
        pEntity.setVisible(false);
        pEntity.detachSelf();
        pEntity.clearEntityModifiers();
        pEntity.clearUpdateHandlers();
        pEntity.dispose();
    }

    //--------------------------------------------
    // GETTER AND SETTER METHODS
    //--------------------------------------------

    // gets the max score of the game
    private int getBestScore()
    {
        return activity.getPreferences(Context.MODE_PRIVATE).getInt("bestScore", 0);
    }

    // sets the max score of the game
    private void setBestScore(int bestScore)
    {
        activity.getPreferences(Context.MODE_PRIVATE).edit().putInt("bestScore", bestScore).apply();
    }
}
