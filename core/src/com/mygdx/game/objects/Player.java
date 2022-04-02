package com.mygdx.game.objects;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Renderer {
    public Animation<TextureRegion> texture;

    public Player()
    {

        boolean isAlive = true;
        boolean isShooting = true;
        //animation
        if(isAlive){
            texture = loadAnimationFromSheet("Assets/player/moving.png",6,3,0.15f);
        } else if (!isAlive){
            texture = loadAnimationFromSheet("Assets/player/dying.png",4,5, 0.15f);
        } else if(!isShooting){
            texture = loadAnimationFromSheet("Assets/player/shooting.png",4,3,0.15f);
        }
    }
}
