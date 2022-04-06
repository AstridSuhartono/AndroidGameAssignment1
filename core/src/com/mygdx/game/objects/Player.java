package com.mygdx.game.objects;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Renderer {
    public Animation<TextureRegion> aliveTexture;
    public Animation<TextureRegion> deadTexture;
    public Animation<TextureRegion> shootTexture;
    public enum State {alive,dead,shoot};
    State playerState;

    public Player()
    {
        playerState = State.alive;
        aliveTexture = loadAnimationFromSheet("Assets/player/moving.png",6,3,0.05f);
        deadTexture = loadAnimationFromSheet("Assets/player/dying.png",4,5, 0.1f);
        shootTexture = loadAnimationFromSheet("Assets/player/shooting.png",4,3,0.05f);
    }

    public State getState() {
        return playerState;
    }

    public void setState(State state) {
        this.playerState = state;
    }
}
