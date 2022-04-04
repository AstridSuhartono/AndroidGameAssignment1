package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.objects.AirEnemy;
import com.mygdx.game.objects.GroundEnemy;
import com.mygdx.game.objects.ParallaxBackground;
import com.mygdx.game.objects.Player;
import java.util.Iterator;

public class GameScreen implements Screen {
    MyGdxGame game;
    public enum GameState { RUNNING, PAUSED, GAMEOVER}
    GameState gameState = GameState.RUNNING;
    ConstantVal constant;
    //Animations
    public Animation<TextureRegion> playerAliveTexture;
    public Animation<TextureRegion> playerDeadTexture;
    public Animation<TextureRegion> groundEnemyAliveTexture;
    public Animation<TextureRegion> groundEnemyDeadTexture;
    public Animation<TextureRegion> airEnemyTexture;
    private Stage stage;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    Player player;
    GroundEnemy groundEnemy;
    AirEnemy airEnemy;
    Rectangle playerBoxCollider;
    Array<Rectangle> groundEnemies;
    Array<Rectangle> airEnemies;
    Array<Rectangle> projectileColliders;
    //Game clock
    float elapsedTime;
    float stateTime = 0f;
    float lastSpawnTime;
    float lastShotTime;
    //Sound effect in game
    Sound shootSound;
    Music gameMusic;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    public void create() {
        stage = new Stage();
        constant = new ConstantVal();
        spriteBatch = new SpriteBatch();
        Skin skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
        //camera setting
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        Gdx.input.setInputProcessor(stage);
        //background setting
        Array<Texture> staticTextures = new Array<>();
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_05.png")));
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_04.png")));
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_03.png")));
        ParallaxBackground staticBackground = new ParallaxBackground(staticTextures);
        staticBackground.setSize(800, 480);
        staticBackground.setSpeed(0);
        stage.addActor(staticBackground);
        Array<Texture> parallaxTextures = new Array<>();
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_02.png")));
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_01.png")));
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_00.png")));
        parallaxTextures.get(parallaxTextures.size-1).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        ParallaxBackground parallaxBackground = new ParallaxBackground(parallaxTextures);
        parallaxBackground.setSize(800, 480);
        parallaxBackground.setSpeed(1);
        stage.addActor(parallaxBackground);
        //buttons
        final TextButton pauseButton = new TextButton("PAUSE", skin, "default");
        pauseButton.setWidth(200f);
        pauseButton.setHeight(80f);
        pauseButton.setPosition(Gdx.graphics.getWidth()- 200, Gdx.graphics.getHeight() - 100);
        pauseButton.getLabel().setFontScale(2f);
        stage.addActor(pauseButton);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                gameState = GameState.PAUSED;
            }
        });
        //sound and music
        shootSound = Gdx.audio.newSound(Gdx.files.internal("projectile.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("bensoundscifi.mp3"));
        gameMusic.setLooping(true);
        gameMusic.play();
        //initialise a new game
        newGame();
    }

    private void newGame(){
        gameState = GameState.RUNNING;
        // Initialise the stateTime, aka how long the program has been running for.
        elapsedTime = 0f;
        player = new Player();
        airEnemy = new AirEnemy();
        groundEnemy = new GroundEnemy();
        player.setState(0);
        groundEnemy.setState(0);
        playerAliveTexture = player.texture;
        airEnemyTexture = airEnemy.texture;
        groundEnemyAliveTexture = groundEnemy.texture;
        //collider bounds
        playerBoxCollider = new Rectangle();
        playerBoxCollider.x = constant.characterX;
        playerBoxCollider.y = constant.characterY;
        playerBoxCollider.width = constant.width;
        playerBoxCollider.height = constant.height;
        groundEnemies = new Array<>();
        airEnemies = new Array<>();
        projectileColliders = new Array<>();
    }

    /**The main function for updating the game state */
    private void updateWorld(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        if(Gdx.input.justTouched()){
            if(gameState == GameState.GAMEOVER){
                newGame();
            }
        }

        //spawn new enemy configuration
        if(TimeUtils.nanoTime() - lastSpawnTime > 8e+9){
            spawnGroundEnemy();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    spawnAirEnemy();
                }
            }, 5);
        }
        Iterator<Rectangle> iterGround = groundEnemies.iterator();
        while (iterGround.hasNext()) {
            Rectangle groundEnemyBoxCollider = iterGround.next();
            groundEnemyBoxCollider.x -= constant.gMoveSpeed * Gdx.graphics.getDeltaTime();
            if (groundEnemyBoxCollider.x + 78 < 0)
                iterGround.remove();
            if (groundEnemyBoxCollider.overlaps(playerBoxCollider)) {
                player.setState(1);
                groundEnemy.setState(1);
                gameState = GameState.GAMEOVER;
            }
        }
        Iterator<Rectangle> iterAir = airEnemies.iterator();
        while (iterAir.hasNext()) {
            Rectangle airEnemyBoxCollider = iterAir.next();
            airEnemyBoxCollider.x += constant.aMoveSpeed * Gdx.graphics.getDeltaTime();
            if (airEnemyBoxCollider.x + 78 > 800)
                iterAir.remove();
        }
    }

    /**Rendering all the animations inside the game */
    private void drawWorld(){
        // The current alive frame to display
        TextureRegion playerAliveCurrFrame = playerAliveTexture.getKeyFrame(elapsedTime, true);
        TextureRegion airCurrFrame = airEnemyTexture.getKeyFrame(elapsedTime, true);
        TextureRegion groundAliveCurrFrame = groundEnemyAliveTexture.getKeyFrame(elapsedTime, true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //logic to draw the correct animation
        if (player.getState() == 0 && gameState == GameState.RUNNING) {
            spriteBatch.draw(playerAliveCurrFrame, playerBoxCollider.x, playerBoxCollider.y, playerBoxCollider.width, playerBoxCollider.height);
            for (Rectangle airEnemy : airEnemies) {
                spriteBatch.draw(airCurrFrame, airEnemy.x, airEnemy.y, airEnemy.width, airEnemy.height);
            }
            for (Rectangle groundEnemy : groundEnemies) {
                spriteBatch.draw(groundAliveCurrFrame, groundEnemy.x, groundEnemy.y, groundEnemy.width, groundEnemy.height);
            }
        }  else if (player.getState() == 1) {
            //TODO
        }
        spriteBatch.end();
    }

    /**Main game loop, all logic and rendering should be called from in here. */
    public void render(float f) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();
        lastShotTime = TimeUtils.nanoTime();

        stage.draw();
        updateWorld();
        drawWorld();

        //player inputs
        if (Gdx.input.isTouched()) {
            if(Gdx.input.getX() < Gdx.graphics.getWidth()/3){
                if (playerBoxCollider.x > 0) {
                    playerBoxCollider.x -= constant.moveSpeed * Gdx.graphics.getDeltaTime();
                }
            }
            else if (Gdx.input.getX() > Gdx.graphics.getWidth()*2/3){
                if(playerBoxCollider.x + playerBoxCollider.width < 400){
                    playerBoxCollider.x += constant.moveSpeed * Gdx.graphics.getDeltaTime();
                }
            }
            else {
                if(TimeUtils.nanoTime() - lastShotTime > constant.shootDelay){
                    shootSound.play();
                    lastShotTime = TimeUtils.nanoTime();
                }
            }
        }

    }

    @Override
    public void dispose() {
        gameMusic.dispose();
        shootSound.dispose();
        spriteBatch.dispose();
        stage.dispose();
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

    public void spawnGroundEnemy(){
        Rectangle groundEnemyBoxCollider = new Rectangle();
        groundEnemyBoxCollider.x = constant.gCharacterX;
        groundEnemyBoxCollider.y = constant.gCharacterY;
        groundEnemyBoxCollider.width = constant.gWidth;
        groundEnemyBoxCollider.height = constant.gHeight;
        groundEnemies.add(groundEnemyBoxCollider);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    public void spawnAirEnemy(){
        Rectangle airEnemyBoxCollider = new Rectangle();
        airEnemyBoxCollider.x = constant.aCharacterX;
        airEnemyBoxCollider.y = constant.aCharacterY;
        airEnemyBoxCollider.width = constant.aWidth;
        airEnemyBoxCollider.height = constant.aHeight;
        airEnemies.add(airEnemyBoxCollider);
    }

}