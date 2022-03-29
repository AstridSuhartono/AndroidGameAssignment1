package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirEnemy extends Renderer{

    public Animation<TextureRegion> texture;

    public AirEnemy()
    {
        boolean isAlive = true;
        boolean isShooting = false;

        if(isAlive){
            texture = loadAnimationFromSheet("Assets/air_enemy/moving.png",3,6,0.15f);
        } else if (!isAlive){
            texture = loadAnimationFromSheet("Assets/air_enemy/dying.png",9,4, 0.15f);
        } else if(!isShooting){
            texture = loadAnimationFromSheet("Assets/air_enemy/shooting.png",6,6,0.15f);
        }
    }

}
