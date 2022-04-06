package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirEnemy extends Renderer{

    int moving = 0;
    int shoot = 1;
    int state;

    public Animation<TextureRegion> texture;

    public AirEnemy() {
        texture = loadAnimationFromSheet("Assets/air_enemy/moving.png",3,6,0.15f);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
