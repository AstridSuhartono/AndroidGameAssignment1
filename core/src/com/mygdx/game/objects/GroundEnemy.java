package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GroundEnemy extends Renderer{
    public Animation<TextureRegion> aliveTexture;
    public Animation<TextureRegion> deadTexture;
    public enum State {alive,dead};
    State groundState;


    public GroundEnemy() {
        groundState = State.alive;
        //animation
        aliveTexture = loadAnimationFromSheet("Assets/ground_enemy/moving.png",3,6,0.1f);
        deadTexture = loadAnimationFromSheet("Assets/ground_enemy/dying.png",5,6, 0.1f);
    }

    public State getState() {
        return groundState;
    }

    public void setState(State state) {
        this.groundState = state;
    }
}
