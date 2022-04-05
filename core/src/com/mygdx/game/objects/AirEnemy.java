package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirEnemy extends Renderer{

    public Animation<TextureRegion> texture;

    public AirEnemy() {
        texture = loadAnimationFromSheet("Assets/air_enemy/moving.png",3,6,0.15f);

    }
}
