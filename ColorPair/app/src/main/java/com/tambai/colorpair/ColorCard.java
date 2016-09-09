package com.tambai.colorpair;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ColorCard extends TiledSprite
{
    public int cardFace;
    private int flipToFrame;

    public ColorCard(int pCardFace, float pX, float pY, float pWidth, float pHeight,
                     ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager);
        cardFace = pCardFace;
    }

    public void startFlip(int flipToWhichFrame)
    {
        flipToFrame = flipToWhichFrame;
        flip();
    }

    public void flip()
    {
        this.setCurrentTileIndex(flipToFrame);
    }

    public void returnCards()
    {
        MainActivity.firstColorCard.startFlip(0);
        MainActivity.secondColorCard.startFlip(0);
        MainActivity.firstColorCard = null;
        MainActivity.secondColorCard = null;
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
    {
        if (pSceneTouchEvent.isActionDown())
        {
            if (!MainActivity.isFirstClickDone)
            {
                MainActivity.isGameStart = true;
                MainActivity.isFirstClickDone = true;
            }

            if (MainActivity.firstColorCard == null)
            {
                MainActivity.firstColorCard = this;
                MainActivity.firstColorCard.startFlip(this.cardFace);
            }
            else if (MainActivity.firstColorCard == this && MainActivity.secondColorCard == null)
            {
                MainActivity.firstColorCard.startFlip(0);
                MainActivity.firstColorCard = null;
            }
            else if (MainActivity.secondColorCard == null)
            {
                MainActivity.secondColorCard = this;
                MainActivity.secondColorCard.startFlip(this.cardFace);

                if (MainActivity.firstColorCard.cardFace == MainActivity.secondColorCard.cardFace)
                {
                    ColorCard.this.clearUpdateHandlers();
                    MainActivity.firstColorCard.setVisible(false);
                    MainActivity.firstColorCard.detachSelf();
                    MainActivity.secondColorCard.setVisible(false);
                    MainActivity.secondColorCard.detachSelf();
                    MainActivity.firstColorCard = null;
                    MainActivity.secondColorCard = null;

                    MainActivity.addScore();

                    if (MainActivity.cardsLeft <= 0)
                    {
                        MainActivity.isGameOver = true;
                    }
                }
                else
                {
                    MainActivity.subtractScore();
                }
            }
            else
            {
                MainActivity.firstColorCard.startFlip(0);
                MainActivity.secondColorCard.startFlip(0);
                MainActivity.secondColorCard = null;
                MainActivity.firstColorCard = this;
                MainActivity.firstColorCard.startFlip(this.cardFace);
            }
        }

        return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }
}
