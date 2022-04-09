package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class MyGdxGame extends Game implements ApplicationListener {

	// The class with the menu
	public static MenuScreen menuScreen;
	// The class with the game
	public static GameScreen gameScreen;
	public static GameScreen2 gameScreen2;


	@Override
	public void create() {
		gameScreen = new GameScreen(this);
		gameScreen2 = new GameScreen2(this);
		menuScreen = new MenuScreen(this);

		// Change screens to the menu
		setScreen(menuScreen);
	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	// this method calls the super class render
	// which in turn calls the render of the actual screen being used
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}