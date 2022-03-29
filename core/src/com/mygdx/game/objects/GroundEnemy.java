package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GroundEnemy extends Renderer{

    public Animation<TextureRegion> texture;

    public GroundEnemy()
    {
        boolean isAlive = true;
        boolean isShooting = false;

        if(isAlive){
            texture = loadAnimationFromSheet("Assets/ground_enemy/moving.png",3,6,0.15f);
        } else if (!isAlive){
            texture = loadAnimationFromSheet("Assets/ground_enemy/dying.png",5,6, 0.15f);
        } else if(!isShooting){
            texture = loadAnimationFromSheet("Assets/ground_enemy/shooting.png",4,3,0.15f);
        }
    }

}
