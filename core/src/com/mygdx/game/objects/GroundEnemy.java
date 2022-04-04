package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GroundEnemy extends Renderer{
    public Animation<TextureRegion> texture;
    public Animation<TextureRegion> aliveTexture;
    public Animation<TextureRegion> deadTexture;
    public Animation<TextureRegion> shootTexture;
    int alive = 0;
    int dead = 1;
    int shoot = 2;
    int state;

    public GroundEnemy()
    {
        //animation
        aliveTexture = loadAnimationFromSheet("Assets/ground_enemy/moving.png",3,6,0.15f);
        deadTexture = loadAnimationFromSheet("Assets/ground_enemy/dying.png",5,6, 0.15f);
        shootTexture = loadAnimationFromSheet("Assets/ground_enemy/shooting.png",4,3,0.15f);
        if (getState() == alive) texture = aliveTexture;
        else if (getState() == dead) texture = deadTexture;
        else if (getState() == shoot) texture = shootTexture;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
