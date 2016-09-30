package com.tambai.spacedefense.Managers;

import com.tambai.spacedefense.MainActivity;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;

public class ResourceManager
{
    //---------------------------------------
    // VARIABLES
    //---------------------------------------

    private static final ResourceManager INSTANCE = new ResourceManager();

    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;

    private ITexture splashFontTexture;
    public Font splashFont;

    private ITexture menuFontTexture;
    public Font menuFont;

    public Sound clickButton;

    private ITexture gameFontTexture;
    public Font gameFont;

    private BuildableBitmapTextureAtlas gameTextureAtlas;
    public ITextureRegion turretBaseTextureRegion;
    public ITextureRegion turretGunTextureRegion;
    public ITextureRegion enemyUfoTextureRegion;
    public ITextureRegion bulletTextureRegion;

    //---------------------------------------
    // RESOURCE MANAGER
    //---------------------------------------

    public void loadSplashResources()
    {
        FontFactory.setAssetBasePath("fonts/");
        splashFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        splashFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), splashFontTexture, activity.getAssets(),
                "splash_font.ttf", 80f, true, Color.WHITE_ABGR_PACKED_INT, 2f, Color.BLACK_ABGR_PACKED_INT);
        splashFont.load();
    }

    public void unloadSplashResources()
    {
        splashFontTexture = null;
        splashFont.unload();
    }

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuFont();
        loadMenuSoundAndMusic();
    }

    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFont();
        loadGameSoundAndMusic();
    }

    private void loadMenuGraphics()
    {
        // Menu Graphics Here!
    }

    private void loadMenuFont()
    {
        FontFactory.setAssetBasePath("fonts/");
        menuFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuFontTexture, activity.getAssets(),
                "menu_font.TTF", 80f, true, Color.GREEN_ABGR_PACKED_INT, 2f, Color.BLACK_ABGR_PACKED_INT);
        menuFont.load();
    }

    private void loadMenuSoundAndMusic()
    {
        SoundFactory.setAssetBasePath("sfx/");

        try
        {
            clickButton = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "click.mp3");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadGameGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

        turretBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
                activity.getAssets(), "turret_base.png");
        turretGunTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
                activity.getAssets(), "turret_gun.png");
        enemyUfoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
                activity.getAssets(), "enemy_ufo.png");
        bulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
                activity.getAssets(), "bullet.png");

        try
        {
            gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 3, 0));
            gameTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    private void loadGameFont()
    {
        FontFactory.setAssetBasePath("fonts/");
        gameFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        gameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameFontTexture, activity.getAssets(),
                "game_font.ttf", 20f, true, Color.WHITE_ABGR_PACKED_INT, 2f, Color.BLACK_ABGR_PACKED_INT);
        gameFont.load();
    }

    private void loadGameSoundAndMusic()
    {
        // Game Sound and Music Here!
    }

    public void loadMenuTextures()
    {
        gameFont.load();
    }

    public void unloadMenuTextures()
    {
        menuFont.unload();
    }

    public void unloadGameTextures()
    {
        gameFont.unload();
    }

    //---------------------------------------
    // STATIC METHODS
    //---------------------------------------

    public static void readyResourceManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }

    public static ResourceManager getInstance()
    {
        return INSTANCE;
    }
}
