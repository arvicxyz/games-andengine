package com.tambai.fishysmack.Managers;

import android.graphics.Color;

import com.tambai.fishysmack.MainActivity;

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
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * @author Amir Fahd Hadji Usop
 * @since October 2014
 */

/*
 * The ResourceManager is the class that manages all resources.
 * Resources like graphics, fonts and sounds are all created,
 * loaded and built here. It also unloads object factories and
 * set resources to NULL if no longer needed. This class can be
 * called in every part of the program to manage resources
 * inside every scene that there is. By AMIR FAHD HADJI USOP
 */

public class ResourceManager
{
    //--------------------------------------------
    // GLOBAL VARIABLES
    //--------------------------------------------

    private static final ResourceManager INSTANCE = new ResourceManager();

    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;

    //***************
    // SPLASH RESOURCES
    //***************
    private BuildableBitmapTextureAtlas splashTextureAtlas;
    public ITextureRegion tambaiGamesLogoRegion;
    public ITextureRegion splashLoadingBgRegion;
    public Font splashLoadingFont;

    //***************
    // MENU RESOURCES
    //***************
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    public ITextureRegion mainMenuBgRegion;
    public ITextureRegion fishyTaleLogoRegion;
    public TiledTextureRegion playButtonRegion;

    private BuildableBitmapTextureAtlas loadingTextureAtlas;
    public ITextureRegion loadingScreenBgRegion;
    public Font loadingTextFont;

    public Sound clickSound;

    //***************
    // GAME RESOURCES
    //***************
    public BuildableBitmapTextureAtlas gameTextureAtlas;
    public ITextureRegion gameScreenBgRegion;
    public ITextureRegion heroRegion;
    public ITextureRegion enemyRegion;
    public ITextureRegion goldCoinRegion;
    public ITextureRegion groundRegion;
    public ITextureRegion gameOverRegion;
    public TiledTextureRegion playAgainButtonRegion;
    public Font gameScoreFont;
    public Font gameResultFont;

    public Sound flapSound;
    public Sound badingSound;
    public Sound smackSound;

    //--------------------------------------------
    // RESOURCE MANAGER
    //--------------------------------------------

    // loads all SPLASH SCENE resources
    public void loadSplashResources()
    {
        loadSplashGraphics();
        loadSplashFonts();
        loadSplashSounds();
    }

    // loads all MENU SCENE resources
    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuFonts();
        loadMenuSounds();
    }

    // loads all GAME SCENE resources
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameSounds();
    }

    //***************
    // SPLASH
    //***************

    // loads SPLASH SCENE graphics
    private void loadSplashGraphics()
    {
        // sets the asset base path of SPLASH SCENE graphics to "gfx/"
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        // creates buildable bitmap texture atlas for the SPLASH SCENE textures
        this.splashTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
                850, 850, TextureOptions.BILINEAR);

        // creates the SPLASH SCENE textures
        this.tambaiGamesLogoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.splashTextureAtlas,
                activity.getAssets(), "tambai_games_logo.png");
        this.splashLoadingBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.splashTextureAtlas,
                activity.getAssets(), "splash_loading_bg.png");

        // builds and loads the buildable bitmap texture atlas of the SPLASH SCENE
        try
        {
            this.splashTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
            this.splashTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    // loads SPLASH SCENE fonts
    private void loadSplashFonts()
    {
        // sets the asset base path of SPLASH SCENE fonts to "fonts/"
        FontFactory.setAssetBasePath("fonts/");

        // creates a texture for the SPLASH SCENE font
        ITexture splashFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        // creates the SPLASH SCENE font
        this.splashLoadingFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), splashFontTexture,
                activity.getAssets(), "game_font.TTF", 72f, true, Color.WHITE, 5f, Color.BLACK);

        // loads the SPLASH SCENE font
        this.splashLoadingFont.load();
    }

    // loads SPLASH SCENE sounds
    private void loadSplashSounds()
    {
        // TODO
    }

    // unloads all SPLASH SCENE resources
    public void unloadSplashResources()
    {
        // sets texture regions of SPLASH SCENE to null
        this.tambaiGamesLogoRegion = null;
        this.splashLoadingBgRegion = null;

        // unloads object factories of SPLASH SCENE
        this.splashTextureAtlas.unload();
        this.splashLoadingFont.unload();
    }

    //***************
    // MENU
    //***************

    // loads MENU SCENE graphics
    private void loadMenuGraphics()
    {
        // sets the asset base path of MENU SCENE graphics to "gfx/menu/"
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        // creates buildable bitmap texture atlas for the MENU SCENE textures
        this.menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
                1024, 1024, TextureOptions.BILINEAR);
        this.loadingTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
                1024, 1024, TextureOptions.BILINEAR);

        // creates the MENU SCENE textures

        // main
        this.mainMenuBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuTextureAtlas,
                activity.getAssets(), "main_menu_bg.png");
        this.fishyTaleLogoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.menuTextureAtlas,
                activity.getAssets(), "fishy_tale_logo.png");
        this.playButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.menuTextureAtlas,
                activity.getAssets(), "play_button.png", 1, 2);

        // loading
        this.loadingScreenBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.loadingTextureAtlas,
                activity.getAssets(), "loading_screen_bg.png");

        // builds and loads the buildable bitmap texture atlas of the MENU SCENE
        try
        {
            this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
            this.loadingTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));

            this.menuTextureAtlas.load();
            this.loadingTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    // loads MENU SCENE fonts
    private void loadMenuFonts()
    {
        // sets the asset base path of MENU SCENE fonts to "fonts/"
        FontFactory.setAssetBasePath("fonts/");

        // creates a texture for the MENU SCENE font
        ITexture loadingFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        // creates the MENU SCENE font
        this.loadingTextFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), loadingFontTexture,
                activity.getAssets(), "game_font.TTF", 72f, true, Color.WHITE, 5f, Color.BLACK);

        // loads the MENU SCENE font
        this.loadingTextFont.load();
    }

    // loads MENU SCENE sounds
    private void loadMenuSounds()
    {
        SoundFactory.setAssetBasePath("sfx/");

        try
        {
            clickSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "click.mp3");
        }
        catch (IOException e)
        {
            Debug.e(e);
        }
    }

    // unloads all MENU SCENE textures
    public void unloadMenuTextures()
    {
        // unloads texture factories of MENU SCENE
        this.menuTextureAtlas.unload();
    }

    // loads all MENU SCENE textures
    public void loadMenuTextures()
    {
        // loads texture factories of MENU SCENE
        this.menuTextureAtlas.load();
    }

    //***************
    // GAME
    //***************

    // loads GAME SCENE graphics
    private void loadGameGraphics()
    {
        // sets the asset base path of GAME SCENE graphics to "gfx/game/"
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

        // creates buildable bitmap texture atlas for the GAME SCENE textures
        this.gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
                1024, 1024, TextureOptions.BILINEAR);

        // creates the GAME SCENE textures
        this.gameScreenBgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "game_screen_bg.png");
        this.heroRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "hero.png");
        this.enemyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "enemy.png");
        this.goldCoinRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "gold_coin.png");
        this.groundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "ground.png");
        this.gameOverRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "game_over.png");
        this.playAgainButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.gameTextureAtlas,
                activity.getAssets(), "play_again_button.png", 1, 2);

        // builds and loads the buildable bitmap texture atlas of the GAME SCENE
        try
        {
            this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
            this.gameTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    // loads GAME SCENE fonts
    private void loadGameFonts()
    {
        // sets the asset base path of GAME SCENE fonts to "fonts/"
        FontFactory.setAssetBasePath("fonts/");

        // creates a texture for the GAME SCENE font
        ITexture gameFontScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        ITexture gameFontResultTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        // creates the GAME SCENE font
        this.gameScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameFontScoreTexture,
                activity.getAssets(), "game_font.TTF", 80f, true, Color.WHITE, 5f, Color.BLACK);
        this.gameResultFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameFontResultTexture,
                activity.getAssets(), "game_font.TTF", 50f, true, Color.WHITE, 3f, Color.BLACK);

        // loads the GAME SCENE font
        this.gameScoreFont.load();
        this.gameResultFont.load();
    }

    // loads GAME SCENE sounds
    private void loadGameSounds()
    {
        SoundFactory.setAssetBasePath("sfx/");

        try
        {
            clickSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "click.mp3");
            flapSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "flap.wav");
            badingSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "bading.wav");
            smackSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "smack.wav");
        }
        catch (IOException e)
        {
            Debug.e(e);
        }
    }

    // unloads all GAME SCENE resources
    public void unloadGameResources()
    {
        // sets texture regions of GAME SCENE to null
        this.gameScreenBgRegion = null;

        // unloads object factories of GAME SCENE
        this.gameTextureAtlas.unload();
        this.gameScoreFont.unload();
    }

    //--------------------------------------------
    // GETTERS AND SETTERS
    //--------------------------------------------

    // sets all the engine, activity, camera, vertex buffer object manager ready for the game
    public static void readyResourceManager(Engine pEngine, MainActivity pActivity,
                                            Camera pCamera, VertexBufferObjectManager pVbom)
    {
        getInstance().engine = pEngine;
        getInstance().activity = pActivity;
        getInstance().camera = pCamera;
        getInstance().vbom = pVbom;
    }

    // gets an instance of the resource manager
    public static ResourceManager getInstance()
    {
        return INSTANCE;
    }
}
