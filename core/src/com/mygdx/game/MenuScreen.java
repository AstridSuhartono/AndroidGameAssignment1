package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuScreen implements Screen {

    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"

    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;

    // constructor to keep a reference to the main Game class
    public MenuScreen(MyGdxGame game) {
        this.game = game;
    }

    public void create() {
        skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
        stage = new Stage();
        batch = new SpriteBatch();

        final TextButton playButton = new TextButton("PLAY", skin, "default");
        playButton.setWidth(300f);
        playButton.setHeight(200f);
        playButton.setPosition(Gdx.graphics.getWidth() /2 - 150f, Gdx.graphics.getHeight()/2);
        playButton.getLabel().setFontScale(4f);

        final TextButton exitButton = new TextButton("EXIT", skin, "default");
        exitButton.setWidth(300f);
        exitButton.setHeight(200f);
        exitButton.setPosition(Gdx.graphics.getWidth() /2 - 150f, Gdx.graphics.getHeight()/2 - 250f);
        exitButton.getLabel().setFontScale(4f);

        stage.addActor(playButton);
        stage.addActor(exitButton);
        Gdx.input.setInputProcessor(stage);

        playButton.addListener(new ClickListener()
        {
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                game.setScreen(MyGdxGame.gameScreen);
            }
        });
        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });
    }

    public void render(float f) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        stage.draw();
        batch.end();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void show() {
        create();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}