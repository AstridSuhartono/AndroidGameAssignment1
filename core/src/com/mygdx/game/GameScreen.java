package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.AirEnemy;
import com.mygdx.game.objects.GroundEnemy;
import com.mygdx.game.objects.ParallaxBackground;
import com.mygdx.game.objects.Player;

public class GameScreen implements Screen {

    public enum GameState {PLAYING, PAUSED, END}

    public Animation<TextureRegion> playerTexture;
    public Animation<TextureRegion> groundEnemyTexture;
    public Animation<TextureRegion> airEnemyTexture;
    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"
    ConstantVal constant;
    GameState gameState = GameState.PLAYING;
    public static final float SHOOT_COOLDOWN_TIME = 1f;
    private Stage stage;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    private boolean isPaused;
    Player player;
    GroundEnemy groundEnemy;
    AirEnemy airEnemy;

    //Game clock
    long lastTime;
    float elapsedTime;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    public void create() {
        Gdx.app.log("GameScreen: ","menuScreen create");
        stage = new Stage();
        spriteBatch = new SpriteBatch();

        //camera setting
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,w, h);

        isPaused = false;
        Gdx.input.setInputProcessor(stage);

        Array<Texture> backgroundTextures = new Array<>();
        backgroundTextures.add(new Texture(Gdx.files.internal("Assets/background/background_05.png")));
        for(int i = 2; i >= 0; i-=2){
            backgroundTextures.add(new Texture(Gdx.files.internal("Assets/background/background_0"+i+".png")));
            backgroundTextures.get(backgroundTextures.size-1).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        }
        ParallaxBackground parallaxBackground = new ParallaxBackground(backgroundTextures);
        parallaxBackground.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        parallaxBackground.setSpeed(1);
        stage.addActor(parallaxBackground);

        newGame();
    }

    private void newGame(){
        gameState = GameState.PLAYING;

        lastTime = System.currentTimeMillis();
        // Initialise the stateTime, aka how long the program has been running for.
        elapsedTime = 0.0f;

        constant = new ConstantVal();
        player = new Player();
        airEnemy = new AirEnemy();
        groundEnemy = new GroundEnemy();
        playerTexture = player.texture;
        airEnemyTexture = airEnemy.texture;
        groundEnemyTexture = groundEnemy.texture;
    }
    public void render(float f) {
        Gdx.app.log("GameScreen: ","gameScreen render");
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();
        //first current frame of moving
        // The current frame to display
        TextureRegion playerCurrFrame = (TextureRegion) playerTexture.getKeyFrame(elapsedTime, true);
        TextureRegion airCurrFrame = (TextureRegion) airEnemyTexture.getKeyFrame(elapsedTime, true);
        TextureRegion groundCurrFrame = (TextureRegion) groundEnemyTexture.getKeyFrame(elapsedTime, true);

        camera.update();
        //Apply the camera's transform to the SpriteBatch so the character is drawn in the correct
        //position on screen.
        spriteBatch.setProjectionMatrix(camera.combined);

        stage.draw();
        spriteBatch.begin();
        spriteBatch.draw(playerCurrFrame,constant.characterX, constant.characterY);
        spriteBatch.draw(airCurrFrame,constant.aCharacterX, constant.aCharacterY);
        spriteBatch.draw(groundCurrFrame, constant.gCharacterX, constant.gCharacterY);
        spriteBatch.end();

    }

    @Override
    public void dispose() { }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void show() {
        Gdx.app.log("GameScreen: ","gameScreen show called");
        create();
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen: ","gameScreen hide called");
        Gdx.input.setInputProcessor(null);
    }
}