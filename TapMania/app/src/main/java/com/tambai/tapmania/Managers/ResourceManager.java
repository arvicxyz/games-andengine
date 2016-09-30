package com.tambai.tapmania.Managers;

import android.graphics.Color;

import com.tambai.tapmania.MainActivity;

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
import org.andengine.util.debug.Debug;

public class ResourceManager
{
    //-----------------------------------------
    // VARIABLES
    //-----------------------------------------

    private static final ResourceManager INSTANCE = new ResourceManager();

    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;

    //-----------------------------------------
    // TEXTURE ATLASES & TEXTURE REGIONS
    //-----------------------------------------

    private BuildableBitmapTextureAtlas menuScreenTextureAtlas;
    public ITextureRegion playButtonRegion;

    //-----------------------------------------
    // FONTS
    //-----------------------------------------

    public Font splashFont;
    public Font gameScoreFont;
    public Font gameSpeedTimeFont;

    //-----------------------------------------
    // RESOURCE MANAGER
    //-----------------------------------------

    public void loadMenuResources()
    {
        loadMenuGraphics();
    }

    public void loadGameResources()
    {
        loadGameGraphics();
    }

    public void unloadGameResources()
    {
        gameScoreFont.unload();
        gameSpeedTimeFont.unload();
    }

    private void loadMenuGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
        FontFactory.setAssetBasePath("fonts/");
        menuScreenTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
                400, 100, TextureOptions.BILINEAR);
        playButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuScreenTextureAtlas,
                activity.getAssets(), "play_button.png");

        try
        {
            menuScreenTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource,
                    BitmapTextureAtlas>(0, 0, 0));
            menuScreenTextureAtlas.load();
        }
        catch (TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    private void loadGameGraphics()
    {
        final ITexture gameScoreFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final ITexture gameSpeedFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
                256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        gameScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameScoreFontTexture,
                activity.getAssets(), "game_font.TTF", 120, true, Color.WHITE, 2, Color.BLACK);
        gameScoreFont.load();
        gameSpeedTimeFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameSpeedFontTexture,
                activity.getAssets(), "splash_font.OTF", 20, true, Color.BLACK, 1, Color.BLACK);
        gameSpeedTimeFont.load();
    }

    //-----------------------------------------
    // STATIC METHODS
    //-----------------------------------------

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
