package com.tambai.spacedefense.Scenes;

import com.tambai.spacedefense.Managers.SceneManager;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class GameScene extends BaseScene implements IOnSceneTouchListener
{
    private int lifeToSubtract = 1;
    private int scoreToAdd = 1;

    private HUD gameHUD;
    private Text lifeText;
    private int life = 5;
    private Text scoreText;
    private int score = 0;
    private boolean isGameOver = false;

    private Sprite gunSprite;
    private Sprite baseSprite;
    private Sprite bulletSprite;

    private LinkedList<Sprite> enemyLL;
    private LinkedList<Sprite> enemiesToBeAdded;

    private LinkedList<Sprite> bulletsLL;
    private LinkedList<Sprite> bulletsToBeAdded;

    @Override
    public void createScene()
    {
        engine.registerUpdateHandler(new FPSLogger());

        this.getBackground().setColor(Color.CYAN);
        this.setOnSceneTouchListener(this);

        createGameHUD();
        createGameHeroTurret();

        enemyLL = new LinkedList<Sprite>();
        enemiesToBeAdded = new LinkedList<Sprite>();

        bulletsLL = new LinkedList<Sprite>();
        bulletsToBeAdded = new LinkedList<Sprite>();

        createSpriteSpawnTimeHandler();
    }

    @Override
    public void onBackKeyPressed()
    {
        SceneManager.getInstance().loadMenuScene();
    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
        return SceneManager.SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
        camera.setHUD(null);
        camera.setCenter(camera.getWidth() / 2, camera.getHeight() / 2);
    }

    private void createGameHUD()
    {
        gameHUD = new HUD();

        lifeText = new Text(30, camera.getHeight() - 30, resourceManager.gameFont, "Life: 0123456789",
                new TextOptions(HorizontalAlign.LEFT), vbom);
        lifeText.setAnchorCenter(0, 0);
        lifeText.setText("Life: 5");
        gameHUD.attachChild(lifeText);

        scoreText = new Text(30, camera.getHeight() - 60, resourceManager.gameFont, "Score: 0123456789",
                new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setAnchorCenter(0, 0);
        scoreText.setText("Score: 0");
        gameHUD.attachChild(scoreText);

        camera.setHUD(gameHUD);
    }

    private void createGameHeroTurret()
    {
        gunSprite = new Sprite(resourceManager.turretBaseTextureRegion.getWidth() / 2,
                resourceManager.turretBaseTextureRegion.getHeight() / 2, resourceManager.turretGunTextureRegion, vbom);
        baseSprite = new Sprite(resourceManager.turretBaseTextureRegion.getWidth() / 2,
                resourceManager.turretBaseTextureRegion.getHeight() / 2, resourceManager.turretBaseTextureRegion, vbom);
        attachChild(gunSprite);
        attachChild(baseSprite);
    }

    private void addScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + String.valueOf(score).toString());
    }

    private void subtractLife(int i)
    {
        life -= i;
        lifeText.setText("Life: " + String.valueOf(life).toString());
    }

    private void addTarget()
    {
        Random rnd = new Random();

        int x = (int) (camera.getWidth() + resourceManager.enemyUfoTextureRegion.getWidth());
        int minY = (int) (resourceManager.enemyUfoTextureRegion.getHeight() + resourceManager.turretGunTextureRegion.getHeight());
        int maxY = (int) (camera.getHeight() - resourceManager.enemyUfoTextureRegion.getHeight());
        int rangeY = maxY - minY;
        int y = rnd.nextInt(rangeY) + minY;

        Sprite enemySprite = new Sprite(x, y, resourceManager.enemyUfoTextureRegion.deepCopy(), vbom);
        attachChild(enemySprite);

        int minDuration = 6;
        int maxDuration = 10;

        if (score >= 100)
        {
            minDuration = 4;
            maxDuration = 8;

            if (score >= 500)
            {
                minDuration = 2;
                maxDuration = 6;

                if (score >= 1000)
                {
                    minDuration = 1;
                    maxDuration = 4;
                }
            }
        }

        int rangeDuration = maxDuration - minDuration;
        int actualDuration = rnd.nextInt(rangeDuration) + minDuration;

        MoveXModifier mod = new MoveXModifier(actualDuration, enemySprite.getX(), -enemySprite.getWidth());
        enemySprite.registerEntityModifier(mod.deepCopy());
        enemiesToBeAdded.add(enemySprite);
    }

    private void createSpriteSpawnTimeHandler()
    {
        TimerHandler spriteTimerHandler;
        float mEffectSpawnDelay = 1f;

        if (score >= 100)
        {
            mEffectSpawnDelay = 0.8f;

            if (score >= 500)
            {
                mEffectSpawnDelay = 0.6f;

                if (score >= 1000)
                {
                    mEffectSpawnDelay = 0.4f;
                }
            }
        }

        spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                addTarget();
            }
        });

        this.registerUpdateHandler(spriteTimerHandler);

        IUpdateHandler detect = new IUpdateHandler()
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                Iterator<Sprite> enemies = enemyLL.iterator();
                Sprite _enemy;
                boolean hit = false;

                while (enemies.hasNext())
                {
                    _enemy = enemies.next();

                    if (_enemy.getX() <= -_enemy.getWidth())
                    {
                        removeSprite(_enemy, enemies);
                        subtractLife(lifeToSubtract);

                        if (life <= 0 && !isGameOver)
                        {
                            lifeToSubtract = 0;
                            scoreToAdd = 0;
                            isGameOver = true;

                            Text gameOverText = new Text(camera.getWidth() / 2, camera.getHeight() / 2,
                                    resourceManager.gameFont, "Your score is\n " + score + "\nGAME OVER!",
                                    new TextOptions(HorizontalAlign.CENTER), vbom);
                            gameOverText.setColor(Color.RED);
                            attachChild(gameOverText);
                        }

                        break;
                    }

                    Iterator<Sprite> bullets = bulletsLL.iterator();
                    Sprite _bullet;

                    while (bullets.hasNext())
                    {
                        _bullet = bullets.next();

                        if (_bullet.getX() >= camera.getWidth()
                                || _bullet.getY() >= camera.getHeight()
                                + _bullet.getHeight()
                                || _bullet.getY() <= -_bullet.getHeight())
                        {
                            removeSprite(_bullet, bullets);
                            continue;
                        }

                        if (_enemy.collidesWith(_bullet))
                        {
                            removeSprite(_bullet, bullets);
                            hit = true;
                            break;
                        }
                    }

                    if (hit)
                    {
                        removeSprite(_enemy, enemies);
                        addScore(scoreToAdd);
                        hit = false;
                    }

                }

                bulletsLL.addAll(bulletsToBeAdded);
                bulletsToBeAdded.clear();

                enemyLL.addAll(enemiesToBeAdded);
                enemiesToBeAdded.clear();
            }

            @Override
            public void reset()
            {
            }
        };

        this.registerUpdateHandler(detect);
    }

    private void removeSprite(final Sprite pSprite, Iterator<Sprite> pIterator)
    {
        engine.runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                detachChild(pSprite);
            }
        });

        pIterator.remove();
    }

    private void shootProjectile(final float pX, final float pY)
    {
        int offX = (int) (pX - gunSprite.getX());
        int offY = (int) (pY - gunSprite.getY());

        if (offX <= 0)
        {
            return;
        }

        FadeInModifier fadeIn = new FadeInModifier(0.5f);

        bulletSprite = new Sprite(gunSprite.getX(), gunSprite.getY(), resourceManager.bulletTextureRegion.deepCopy(), vbom);
        bulletSprite.setScale(0.75f);
        bulletSprite.registerEntityModifier(fadeIn);
        attachChild(bulletSprite);

        int realX = (int) (camera.getWidth() + bulletSprite.getWidth() / 2.0f);
        float ratio = (float) offY / (float) offX;
        int realY = (int) ((realX * ratio) + bulletSprite.getY());

        int offRealX = (int) (realX - bulletSprite.getX());
        int offRealY = (int) (realY - bulletSprite.getY());
        float length = (float) Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
        float velocity = 480.0f / 1.0f;
        float realMoveDuration = length / velocity;

        MoveModifier mod = new MoveModifier(realMoveDuration, bulletSprite.getX(), bulletSprite.getY(), realX, realY);
        bulletSprite.registerEntityModifier(mod.deepCopy());
        bulletsToBeAdded.add(bulletSprite);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {
        if (pSceneTouchEvent.isActionDown())
        {
            final float touchX = pSceneTouchEvent.getX();
            final float touchY = pSceneTouchEvent.getY();

            final float dX = touchX - gunSprite.getX();
            final float dY = touchY - gunSprite.getY();

            final float angle = (float) Math.atan2(-dY, dX);
            final float rotation = MathUtils.radToDeg(angle) + 90;

            gunSprite.setRotation(rotation);
            shootProjectile(touchX, touchY);

            return true;
        }

        return false;
    }
}
