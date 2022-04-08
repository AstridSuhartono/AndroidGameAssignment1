package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirEnemy extends Renderer{
    public Animation<TextureRegion> aliveTexture;
    public Animation<TextureRegion> shootTexture;
    public enum State {alive,shoot};
    State airState;

    public AirEnemy() {
        airState = State.alive;
        aliveTexture = loadAnimationFromSheet("Assets/air_enemy/moving.png",3,6,0.05f);
        shootTexture = loadAnimationFromSheet("Assets/air_enemy/shooting.png",6,6,0.05f);

    }

    public State getState() {
        return airState;
    }

    public void setState(State state) {
        this.airState = state;
    }
}
