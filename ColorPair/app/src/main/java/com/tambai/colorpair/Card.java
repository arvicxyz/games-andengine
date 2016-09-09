package com.tambai.colorpair;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Card extends TiledSprite
{
    public int cardFace;
    private int flipToFrame;

    public Card(int pCardFace, float pX, float pY, float pWidth, float pHeight,
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
        MainActivity.firstCard.startFlip(0);
        MainActivity.secondCard.startFlip(0);
        MainActivity.firstCard = null;
        MainActivity.secondCard = null;
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

            if (MainActivity.firstCard == null)
            {
                MainActivity.firstCard = this;
                MainActivity.firstCard.startFlip(this.cardFace);
            }
            else if (MainActivity.firstCard == this && MainActivity.secondCard == null)
            {
                MainActivity.firstCard.startFlip(0);
                MainActivity.firstCard = null;
            }
            else if (MainActivity.secondCard == null)
            {
                MainActivity.secondCard = this;
                MainActivity.secondCard.startFlip(this.cardFace);

                if (MainActivity.firstCard.cardFace == MainActivity.secondCard.cardFace)
                {
                    Card.this.clearUpdateHandlers();
                    MainActivity.firstCard.setVisible(false);
                    MainActivity.firstCard.detachSelf();
                    MainActivity.secondCard.setVisible(false);
                    MainActivity.secondCard.detachSelf();
                    MainActivity.firstCard = null;
                    MainActivity.secondCard = null;

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
                MainActivity.firstCard.startFlip(0);
                MainActivity.secondCard.startFlip(0);
                MainActivity.secondCard = null;
                MainActivity.firstCard = this;
                MainActivity.firstCard.startFlip(this.cardFace);
            }
        }

        return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }
}
