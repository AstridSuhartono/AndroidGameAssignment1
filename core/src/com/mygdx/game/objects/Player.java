package com.mygdx.game.objects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Player extends Renderer {
    public Animation<TextureRegion> texture;

    public Player()
    {
        //player characteristics
        float movementSpeed;

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
