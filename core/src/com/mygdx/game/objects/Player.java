package com.mygdx.game.objects;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Renderer {
    public Animation<TextureRegion> texture;
    public Animation<TextureRegion> aliveTexture;
    public Animation<TextureRegion> deadTexture;
    public Animation<TextureRegion> shootTexture;
    int alive = 0;
    int dead = 1;
    int shoot = 2;
    int state;
    float stateTime;

    public Player()
    {
        state = alive;
        stateTime = 0;
        aliveTexture = loadAnimationFromSheet("Assets/player/moving.png",6,3,0.15f);
        deadTexture = loadAnimationFromSheet("Assets/player/dying.png",4,5, 0.15f);
        shootTexture = loadAnimationFromSheet("Assets/player/shooting.png",4,3,0.15f);

        //animation
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
