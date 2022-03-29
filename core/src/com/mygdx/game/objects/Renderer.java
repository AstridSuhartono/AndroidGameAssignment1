package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Renderer{
    /**
     * Creates an animation from a spritesheet: a rectangular grid of images stored in a single file.
     * @param fileName name of file containing spritesheet
     * @param rows number of rows of images in spritesheet
     * @param cols number of columns of images in spritesheet
     * @return animation created (useful for storing multiple animations)
     */
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float duration)
    {
        Texture textureSheet = new Texture(Gdx.files.internal(fileName));
        int frameWidth = textureSheet.getWidth() / cols;
        int frameHeight = textureSheet.getHeight() / rows;
        TextureRegion[][] temp = TextureRegion.split(textureSheet, frameWidth, frameHeight);
        Array<TextureRegion> textureFrames = new Array<TextureRegion>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                textureFrames.add( temp[r][c] );
        Animation<TextureRegion> anim = new Animation<TextureRegion>(duration, textureFrames);
        return anim;
    }

}
