package com.mygdx.game;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import com.mygdx.game.objects.Renderer;
import java.util.Iterator;

public class GameScreen extends Renderer implements Screen  {
    MyGdxGame game;
    public enum GameState { RUNNING, PAUSED, GAMEOVER}
    GameState gameState;
    private Skin skin;
    ConstantVal constant;
    private Integer score;
    private Label scoreLabel;
    private Label scoreAcumLabel;
    //Animations and textures
    Animation<TextureRegion> playerAliveTexture, playerDeadTexture, playerShootTexture ;
    Animation<TextureRegion> groundEnemyAliveTexture, groundEnemyDeadTexture;
    Animation<TextureRegion> airEnemyAliveTexture, airEnemyShootTexture;
    Animation<TextureRegion> explosionSmallTexture, explosionBigTexture;
    Texture playerProjectileImage, airEnemyProjectileImage;
    //game background and setting
    ParallaxBackground staticBackground, parallaxBackground;
    private Stage stage;
    private Stage gameOverStage;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    public BitmapFont font;
    //Object
    Player player;
    GroundEnemy groundEnemy;
    AirEnemy airEnemy;
    Rectangle playerBoxCollider;
    Rectangle playerProjectile;
    Rectangle airEnemyProjectile;
    Array<Rectangle> airEnemies, groundEnemies;
    Array<Rectangle> playerProjectiles, airEnemyProjectiles;
    boolean isActiveProjectile = true;
    boolean isActiveAirProjectile = true;
    //Game clock
    float deltaTime;
    float elapsedTime;
    float elapsedTimeGround;
    float elapsedTimeAir;
    float lastGroundSpawnTime;
    float lastAirSpawnTime;
    float lastShotTime;
    //Sound in game
    Sound shootSound, explosionSound, playerDieSound, projectileSound;
    Music gameMusic;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    public void create() {
        stage = new Stage();
        gameOverStage = new Stage();
        constant = new ConstantVal();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
        //camera setting
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        //background setting
        Array<Texture> staticTextures = new Array<>();
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_05.png")));
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_04.png")));
        staticTextures.add(new Texture(Gdx.files.internal("Assets/background/background_03.png")));
        staticBackground = new ParallaxBackground(staticTextures);
        staticBackground.setSize(800, 480);
        staticBackground.setSpeed(0);
        stage.addActor(staticBackground);
        Array<Texture> parallaxTextures = new Array<>();
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_02.png")));
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_01.png")));
        parallaxTextures.add(new Texture(Gdx.files.internal("Assets/background/background_00.png")));
        parallaxTextures.get(parallaxTextures.size-1).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        parallaxBackground = new ParallaxBackground(parallaxTextures);
        parallaxBackground.setSize(800, 480);
        parallaxBackground.setSpeed(2);
        stage.addActor(parallaxBackground);
        //sound and music
        shootSound = Gdx.audio.newSound(Gdx.files.internal("projectile.wav"));
        projectileSound = Gdx.audio.newSound(Gdx.files.internal("airprojectile.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        playerDieSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("bensoundscifi.mp3"));
        gameMusic.setVolume(0.6f);
        gameMusic.setLooping(true);
        gameMusic.play();
        //initialise a new game
        newGame();
    }

    private void newGame(){
        gameState = GameState.RUNNING;
        Gdx.input.setInputProcessor(stage);
        gameOverStage.clear();
        score = 0;
        parallaxBackground.setSpeed(2);
        // Initialise the state time, aka how long the program has been running for.
        deltaTime = 0f;
        elapsedTime = 0f;
        elapsedTimeAir = 0f;
        elapsedTimeGround = 0f;
        lastShotTime = 0f;
        lastGroundSpawnTime = 0f;
        lastAirSpawnTime = 0f;
        player = new Player();
        airEnemy = new AirEnemy();
        groundEnemy = new GroundEnemy();
        isActiveProjectile = false;
        isActiveAirProjectile = false;
        //set the texture required for draw
        playerAliveTexture = player.aliveTexture;
        playerDeadTexture = player.deadTexture;
        playerShootTexture = player.shootTexture;
        airEnemyAliveTexture = airEnemy.aliveTexture;
        airEnemyShootTexture = airEnemy.shootTexture;
        groundEnemyAliveTexture = groundEnemy.aliveTexture;
        groundEnemyDeadTexture = groundEnemy.deadTexture;
        playerProjectileImage = new Texture(Gdx.files.internal("Assets/player/projectile.png"));
        airEnemyProjectileImage = new Texture(Gdx.files.internal("Assets/air_enemy/projectile.png"));
        explosionBigTexture = loadAnimationFromSheet("Assets/explosion/explosion_big.png",3,5,0.15f);
        explosionSmallTexture = loadAnimationFromSheet("Assets/explosion/explosion_small.png",1,7,0.2f);
        scoreAcumLabel = new Label(String.format("%05d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreAcumLabel.setFontScale(3);
        scoreAcumLabel.setPosition(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight() - 90);
        scoreLabel = new Label("SCORE:", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setFontScale(3);
        scoreLabel.setPosition(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight() - 50);
        stage.addActor(scoreAcumLabel);
        stage.addActor(scoreLabel);
        //colliders for player and enemies
        playerBoxCollider = new Rectangle(constant.characterX, constant.characterY, constant.width, constant.height);
        groundEnemies = new Array<>();
        airEnemies = new Array<>();
        playerProjectiles = new Array<>();
        airEnemyProjectiles = new Array<>();
    }

    /**The main function for updating the game state */
    private void updateWorld() {
        elapsedTime += Gdx.graphics.getDeltaTime();
        elapsedTimeGround += Gdx.graphics.getDeltaTime();
        elapsedTimeAir += Gdx.graphics.getDeltaTime();
        if (score >= 500){
            gameMusic.stop();
            game.setScreen(MyGdxGame.gameScreen2);
        }
        //the game loop while game state is running
        if (gameState == GameState.RUNNING) {
            if (Gdx.input.isTouched() && player.getState() == Player.State.alive) {
                if (Gdx.input.getX() < Gdx.graphics.getWidth() / 3) {
                    if (playerBoxCollider.x > 0) {
                        playerBoxCollider.x -= constant.moveSpeed * Gdx.graphics.getDeltaTime();
                    }
                } else if (Gdx.input.getX() > Gdx.graphics.getWidth() * 2 / 3) {
                    if (playerBoxCollider.x + playerBoxCollider.width < 400) {
                        playerBoxCollider.x += constant.moveSpeed * Gdx.graphics.getDeltaTime();
                    }
                } else {
                    if ((TimeUtils.nanoTime() - lastShotTime) > constant.shootDelay) {
                        player.setState(Player.State.shoot);
                        shootSound.play();
                        elapsedTime = 0;
                        playerProjectile = new Rectangle(playerBoxCollider.getX()+55, playerBoxCollider.getY()+35, constant.pWidth, constant.pHeight);
                        playerProjectiles.add(playerProjectile);
                        isActiveProjectile = true;
                        lastShotTime = TimeUtils.nanoTime();
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                player.setState(Player.State.alive);
                            }
                        }, 1f);
                    }
                }
            }
            shootPlayerProjectile();
            //spawn new enemies after certain time has pass
            if (deltaTime >= 5f) {
                if (TimeUtils.nanoTime() - lastGroundSpawnTime > 6e+9) {
                    spawnGroundEnemy();
                    groundEnemy.setState(GroundEnemy.State.alive);
                }
            }
            if (deltaTime >= 8f){
                if (TimeUtils.nanoTime() - lastAirSpawnTime > 9e+9) {
                    spawnAirEnemy();
                    airEnemy.setState(AirEnemy.State.alive);
                }
            }
            //collision and states of ground enemy
            Iterator<Rectangle> iterGround = groundEnemies.iterator();
            while (iterGround.hasNext()) {
                Rectangle groundEnemyBoxCollider = iterGround.next();
                groundEnemyBoxCollider.x -= constant.gMoveSpeed * Gdx.graphics.getDeltaTime();
                if (groundEnemyBoxCollider.x - 78 < 0)
                    iterGround.remove();
                if (groundEnemy.getState() == GroundEnemy.State.alive) {
                    if (groundEnemyBoxCollider.overlaps(playerBoxCollider)) {
                        player.setState(Player.State.dead);
                        playerDieSound.play();
                        elapsedTime = 0;
                        elapsedTime += Gdx.graphics.getDeltaTime();
                        gameState = GameState.GAMEOVER;
                        parallaxBackground.setSpeed(0);
                    }
                    if (isActiveProjectile) {
                        if (groundEnemyBoxCollider.overlaps(playerProjectile)) {
                            groundEnemy.setState(GroundEnemy.State.dead);
                            explosionSound.play();
                            isActiveProjectile = false;
                            elapsedTimeGround = 0;
                            elapsedTimeGround += Gdx.graphics.getDeltaTime();
                            score += 100;
                            scoreAcumLabel.setText(String.format("%05d", score));
                        }
                    }
                }
            }
            //collision and states of air enemy
            Iterator<Rectangle> iterAir = airEnemies.iterator();
            while (iterAir.hasNext()) {
                Rectangle airEnemyBoxCollider = iterAir.next();
                if (airEnemyBoxCollider.x > 800) { iterAir.remove(); }
                if (airEnemy.getState() == AirEnemy.State.alive){
                    airEnemyBoxCollider.x += constant.aMoveSpeed * Gdx.graphics.getDeltaTime();
                }
                if (airEnemyBoxCollider.getX() >= (playerBoxCollider.getX() - 55 ) && airEnemyBoxCollider.getX() <= (playerBoxCollider.getX() -50 ) && airEnemy.getState() == AirEnemy.State.alive) {
                    airEnemy.setState(AirEnemy.State.shoot);
                    projectileSound.play();
                    elapsedTimeAir = 0;
                    elapsedTimeAir += Gdx.graphics.getDeltaTime();
                    airEnemyProjectile = new Rectangle(airEnemyBoxCollider.getX() + 82, airEnemyBoxCollider.getY(),30, 30);
                    airEnemyProjectiles.add(airEnemyProjectile);
                    isActiveAirProjectile = true;
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            airEnemy.setState(AirEnemy.State.alive);
                        }
                    }, 2f);
                }
                if (isActiveAirProjectile) {
                    if (playerBoxCollider.overlaps(airEnemyProjectile)) {
                        player.setState(Player.State.dead);
                        playerDieSound.play();
                        elapsedTime = 0;
                        elapsedTime += Gdx.graphics.getDeltaTime();
                        gameState = GameState.GAMEOVER;
                        parallaxBackground.setSpeed(0);
                    }
                }
            }
            shootAirProjectile();
            //pause button
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
                    if (gameState == GameState.RUNNING){
                        gameState = GameState.PAUSED;
                        parallaxBackground.setSpeed(0);
                    }
                    else if (gameState == GameState.PAUSED){
                        gameState = GameState.RUNNING;
                        parallaxBackground.setSpeed(2);
                    }
                }
            });
        //state when the player died and gamestate is game over
        } else if (gameState == GameState.GAMEOVER) {
            final TextButton restartButton = new TextButton("RESTART", skin, "default");
            restartButton.setWidth(300f);
            restartButton.setHeight(100f);
            restartButton.setPosition(Gdx.graphics.getWidth()/2 - 150, Gdx.graphics.getHeight()/2 - 75);
            restartButton.getLabel().setFontScale(2.2f);
            gameOverStage.addActor(restartButton);
            restartButton.addListener(new ClickListener() {
                @Override
                public void clicked (InputEvent event, float x, float y)
                {
                    restartButton.setVisible(false);
                    gameState = GameState.RUNNING;
                    newGame();
                }
            });
        }
    }

    /**Rendering all the animations inside the game */
    private void drawWorld(){
        // The current frame to display
        TextureRegion playerAliveCurrFrame = playerAliveTexture.getKeyFrame(deltaTime, true);
        TextureRegion playerDeadCurrFrame = playerDeadTexture.getKeyFrame(elapsedTime, false);
        TextureRegion playerShootCurrFrame = playerShootTexture.getKeyFrame(elapsedTime, false);
        TextureRegion playerExplodeCurrFrame = explosionSmallTexture.getKeyFrame(elapsedTime, false);
        TextureRegion airAliveCurrFrame = airEnemyAliveTexture.getKeyFrame(deltaTime, true);
        TextureRegion airShootCurrFrame = airEnemyShootTexture.getKeyFrame(elapsedTimeAir, false);
        TextureRegion groundAliveCurrFrame = groundEnemyAliveTexture.getKeyFrame(deltaTime, true);
        TextureRegion groundDeadCurrFrame = groundEnemyDeadTexture.getKeyFrame(elapsedTimeGround, false);
        TextureRegion groundExplodeCurrFrame = explosionBigTexture.getKeyFrame(elapsedTimeGround, false);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        //start drawing based on the updateWorld() logic to draw the correct animation
        spriteBatch.begin();
        if (gameState == GameState.RUNNING) {
            if (player.getState() == Player.State.alive){
                spriteBatch.draw(playerAliveCurrFrame, playerBoxCollider.x-20, playerBoxCollider.y, 112, 112);
            } else if (player.getState() == Player.State.shoot){
                spriteBatch.draw(playerProjectileImage, playerProjectile.x, playerProjectile.y, playerProjectile.width, playerProjectile.height);
                if(!playerShootTexture.isAnimationFinished(elapsedTime)){
                    spriteBatch.draw(playerShootCurrFrame, playerBoxCollider.x-23, playerBoxCollider.y, 112, 112);
                    if(playerShootTexture.getKeyFrameIndex(elapsedTime) >= 9){
                        player.setState(Player.State.alive);
                    }
                }
            }
            if(airEnemy.getState() == AirEnemy.State.alive){
                for (Rectangle airEnemy : airEnemies) {
                    spriteBatch.draw(airAliveCurrFrame, airEnemy.x, airEnemy.y, airEnemy.width, airEnemy.height);
                }
            } else if(airEnemy.getState() == AirEnemy.State.shoot) {
                for (Rectangle airEnemy : airEnemies) {
                    spriteBatch.draw(airShootCurrFrame, airEnemy.x, airEnemy.y, airEnemy.width, airEnemy.height);
                    spriteBatch.draw(airEnemyProjectileImage, airEnemyProjectile.x, airEnemyProjectile.y, airEnemyProjectile.width, airEnemyProjectile.height);
                    if (airEnemyShootTexture.getKeyFrameIndex(elapsedTimeAir) >= 34 && airEnemyProjectile.getY() < 40) {
                        spriteBatch.draw(airAliveCurrFrame, airEnemy.x, airEnemy.y, airEnemy.width, airEnemy.height);
                    }
                }
            }
            if (groundEnemy.getState() == GroundEnemy.State.alive){
                for (Rectangle groundEnemy : groundEnemies) {
                    spriteBatch.draw(groundAliveCurrFrame, groundEnemy.x-30, groundEnemy.y, 156, groundEnemy.height);
                }
            } else if (groundEnemy.getState() == GroundEnemy.State.dead) {
                for (Rectangle groundEnemy : groundEnemies) {
                    spriteBatch.draw(groundDeadCurrFrame, groundEnemy.x, groundEnemy.y-16, groundEnemy.width+12, groundEnemy.height+12);
                    spriteBatch.draw(groundExplodeCurrFrame, groundEnemy.x, groundEnemy.y, groundEnemy.width+12, groundEnemy.height+12);
                }
            }
        } else if (gameState == GameState.GAMEOVER) {
            if (!playerDeadTexture.isAnimationFinished(elapsedTime)) {
                spriteBatch.draw(playerDeadCurrFrame, playerBoxCollider.x-15, playerBoxCollider.y-8, 124, 124);
                spriteBatch.draw(playerExplodeCurrFrame, playerBoxCollider.x, playerBoxCollider.y-8, playerBoxCollider.width+24, playerBoxCollider.height+24);
            }
            font.setColor(Color.BLUE);
            font.getData().setScale(1);
            font.draw(spriteBatch, "GAME OVER! CLICK THE BUTTON TO RESTART", 250, 300);
        } else if (gameState == GameState.PAUSED){
            font.setColor(Color.BLUE);
            font.getData().setScale(2);
            font.draw(spriteBatch, "GAME PAUSED", 250, 240);
        }
        spriteBatch.end();
    }

    /**Main game loop, all logic and rendering should be called from in here. */
    public void render(float f) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        deltaTime += Gdx.graphics.getDeltaTime();
        stage.draw();
        updateWorld();
        drawWorld();
        if (gameState == GameState.PAUSED) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            deltaTime += 0;
            stage.draw();
            updateWorld();
            drawWorld();
        }
        if (gameState == GameState.GAMEOVER){
            gameOverStage.draw();
            Gdx.input.setInputProcessor(gameOverStage);
        }
    }

    @Override
    public void dispose() {
        gameMusic.dispose();
        font.dispose();
        shootSound.dispose();
        explosionSound.dispose();
        playerDieSound.dispose();
        projectileSound.dispose();
        spriteBatch.dispose();
        gameOverStage.dispose();
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
        Rectangle groundEnemyBoxCollider = new Rectangle(constant.gCharacterX, constant.gCharacterY, constant.gWidth, constant.gHeight);
        groundEnemies.add(groundEnemyBoxCollider);
        lastGroundSpawnTime = TimeUtils.nanoTime();
    }

    public void spawnAirEnemy(){
        Rectangle airEnemyBoxCollider = new Rectangle(constant.aCharacterX, constant.aCharacterY, constant.aWidth, constant.aHeight);
        airEnemies.add(airEnemyBoxCollider);
        lastAirSpawnTime = TimeUtils.nanoTime();
    }

    public void shootPlayerProjectile(){
        Iterator<Rectangle> iterProjectile = playerProjectiles.iterator();
        while (iterProjectile.hasNext()) {
            Rectangle playerProjectile = iterProjectile.next();
            playerProjectile.x += constant.projectileSpeed * Gdx.graphics.getDeltaTime();
            playerProjectile.y -= MathUtils.log(10,constant.playerGravity);
            if (playerProjectile.x > playerBoxCollider.x + 300 || playerProjectile.y < 40 ){
                isActiveProjectile = false;
                iterProjectile.remove();
            }
        }
    }

    public void shootAirProjectile(){
        Iterator<Rectangle> iterAirProjectile = airEnemyProjectiles.iterator();
        while (iterAirProjectile.hasNext()) {
            Rectangle airEnemyProjectile = iterAirProjectile.next();
            airEnemyProjectile.x += 0;
            airEnemyProjectile.y -= constant.enemyProjectileGravity * Gdx.graphics.getDeltaTime();
            if (airEnemyProjectile.y < 30) {
                isActiveAirProjectile = false;
                iterAirProjectile.remove();
            }
        }
    }
}