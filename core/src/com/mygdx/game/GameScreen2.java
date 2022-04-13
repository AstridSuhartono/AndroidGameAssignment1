package com.mygdx.game;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.objects.GroundEnemy;
import com.mygdx.game.objects.ParallaxBackground;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Renderer;

import java.util.Iterator;

public class GameScreen2 extends Renderer implements Screen {
    MyGdxGame game;
    public enum GameState { RUNNING, PAUSED, GAMEOVER, WIN}
    GameState gameState;
    private Skin skin;
    ConstantVal constant;
    //Animations and textures
    Animation<TextureRegion> playerAliveTexture, playerDeadTexture, playerShootTexture;
    Animation<TextureRegion> enemyAliveTexture, enemyDeadTexture, enemyShootTexture;
    Animation<TextureRegion> explosionSmallTexture, explosionBigTexture;
    Texture playerProjectileImage;
    Texture enemyProjectileImage;
    ParallaxBackground staticBackground, parallaxBackground;
    //object
    private Stage stage;
    private Stage gameOverStage;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    public BitmapFont font;
    private Label healthLabel;
    Player player;
    GroundEnemy enemy;
    Rectangle playerBoxCollider;
    Rectangle playerProjectile;
    Rectangle enemyBoxCollider;
    Rectangle enemyProjectile;
    Array<Rectangle> playerProjectiles, enemyProjectiles;
    boolean isActiveProjectile = true;
    boolean isActiveEnemyProjectile = true;
    Integer bossHealth;
    //Game clock
    float deltaTime;
    float elapsedTime;
    float elapsedTimeEnemy;
    float lastShotTime;
    float lastEShootTime;
    //Sound in game
    Sound shootSound, explosionSound, playerDieSound, projectileSound, hitSound;
    Music gameMusic;

    // constructor to keep a reference to the main Game class
    public GameScreen2(MyGdxGame game) {
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
        projectileSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        playerDieSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        hitSound =  Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("bossmusic.wav"));
        gameMusic.setLooping(true);
        gameMusic.play();
        //initialise a new game
        newGame();
    }

    private void newGame(){
        gameState = GameState.RUNNING;
        Gdx.input.setInputProcessor(stage);
        gameOverStage.clear();
        parallaxBackground.setSpeed(2);
        // Initialise the state time, aka how long the program has been running for.
        deltaTime = 0f;
        elapsedTime = 0f;
        elapsedTimeEnemy = 0f;
        lastShotTime = 0f;
        lastEShootTime = 0f;
        bossHealth = constant.health;
        player = new Player();
        enemy = new GroundEnemy();
        isActiveProjectile = false;
        isActiveEnemyProjectile = false;
        //set the texture required for draw
        playerAliveTexture = player.aliveTexture;
        playerDeadTexture = player.deadTexture;
        playerShootTexture = player.shootTexture;
        enemyShootTexture = enemy.shootTexture;
        enemyAliveTexture = enemy.aliveTexture;
        enemyDeadTexture = enemy.deadTexture;
        playerProjectileImage = new Texture(Gdx.files.internal("Assets/player/projectile.png"));
        enemyProjectileImage = new Texture(Gdx.files.internal("Assets/ground_enemy/projectile.png"));
        explosionBigTexture = loadAnimationFromSheet("Assets/explosion/explosion_big.png",3,5,0.15f);
        explosionSmallTexture = loadAnimationFromSheet("Assets/explosion/explosion_small.png",1,7,0.2f);
        healthLabel = new Label(String.format("Health: %d", bossHealth), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthLabel.setFontScale(4);
        healthLabel.setPosition((Gdx.graphics.getWidth() * 2/3)+170, Gdx.graphics.getHeight()/2 +120);
        stage.addActor(healthLabel);
        //colliders for player and enemies
        playerBoxCollider = new Rectangle(constant.characterX, constant.characterY, constant.width, constant.height);
        enemyBoxCollider = new Rectangle(constant.bCharacterX, constant.bCharacterY, constant.bWidth, constant.bHeight);
        playerProjectiles = new Array<>();
        enemyProjectiles = new Array<>();
    }

    /**The main function for updating the game state */
    private void updateWorld() {
        elapsedTime += Gdx.graphics.getDeltaTime();
        elapsedTimeEnemy += Gdx.graphics.getDeltaTime();
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
            if (isActiveProjectile) {
                if (playerProjectile.overlaps(enemyBoxCollider)) {
                    bossHealth -= 1;
                    healthLabel.setText(String.format("%d", bossHealth));
                    hitSound.play();
                    isActiveProjectile = false;
                    if (bossHealth == 0) {
                        enemy.setState(GroundEnemy.State.dead);
                        explosionSound.play();
                        elapsedTimeEnemy = 0;
                        elapsedTimeEnemy += Gdx.graphics.getDeltaTime();
                        gameState = GameState.WIN;
                    }
                }
            }
            if ((TimeUtils.nanoTime() - lastEShootTime) > constant.bossShootDelay && enemy.getState() == GroundEnemy.State.alive) {
                enemy.setState(GroundEnemy.State.shoot);
                projectileSound.play();
                elapsedTimeEnemy = 0;
                elapsedTimeEnemy += Gdx.graphics.getDeltaTime();
                enemyProjectile = new Rectangle(enemyBoxCollider.getX() + 70, enemyBoxCollider.getY() + 200,32, 32);
                enemyProjectiles.add(enemyProjectile);
                isActiveEnemyProjectile = true;
                lastEShootTime = TimeUtils.nanoTime();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        enemy.setState(GroundEnemy.State.alive);
                    }
                }, 4f);
            }
            shootEnemyProjectile();
            if (isActiveEnemyProjectile) {
                if (enemyProjectile.overlaps(playerBoxCollider)) {
                    player.setState(Player.State.dead);
                    playerDieSound.play();
                    elapsedTime = 0;
                    elapsedTime += Gdx.graphics.getDeltaTime();
                    gameState = GameState.GAMEOVER;
                    parallaxBackground.setSpeed(0);
                }
            }
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

    public void drawWorld(){
        TextureRegion playerAliveCurrFrame = playerAliveTexture.getKeyFrame(deltaTime, true);
        TextureRegion playerDeadCurrFrame = playerDeadTexture.getKeyFrame(elapsedTime, false);
        TextureRegion playerShootCurrFrame = playerShootTexture.getKeyFrame(elapsedTime, false);
        TextureRegion playerExplodeCurrFrame = explosionSmallTexture.getKeyFrame(elapsedTime, false);
        TextureRegion groundShootCurrFrame = enemyShootTexture.getKeyFrame(elapsedTimeEnemy, false);
        TextureRegion groundAliveCurrFrame = enemyAliveTexture.getKeyFrame(deltaTime, true);
        TextureRegion groundDeadCurrFrame = enemyDeadTexture.getKeyFrame(elapsedTimeEnemy, false);
        TextureRegion groundExplodeCurrFrame = explosionBigTexture.getKeyFrame(elapsedTimeEnemy, false);
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
            if (enemy.getState() == GroundEnemy.State.alive){
                spriteBatch.draw(groundAliveCurrFrame, enemyBoxCollider.x, enemyBoxCollider.y, enemyBoxCollider.width, enemyBoxCollider.height);
            } else if (enemy.getState() == GroundEnemy.State.dead) {
                spriteBatch.draw(groundDeadCurrFrame, enemyBoxCollider.x, enemyBoxCollider.y-16, enemyBoxCollider.width+12, enemyBoxCollider.height+12);
                spriteBatch.draw(groundExplodeCurrFrame, enemyBoxCollider.x, enemyBoxCollider.y, enemyBoxCollider.width+12, enemyBoxCollider.height+12);
            }else if (enemy.getState() == GroundEnemy.State.shoot){
                spriteBatch.draw(enemyProjectileImage, enemyProjectile.x, enemyProjectile.y, enemyProjectile.width, enemyProjectile.height);
                if(!enemyShootTexture.isAnimationFinished(elapsedTimeEnemy)){
                    spriteBatch.draw(groundShootCurrFrame, enemyBoxCollider.x, enemyBoxCollider.y, enemyBoxCollider.width, enemyBoxCollider.height);
                    if(enemyShootTexture.getKeyFrameIndex(elapsedTimeEnemy) >= 9){
                        enemy.setState(GroundEnemy.State.alive);
                    }
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
            font.draw(spriteBatch, "GAME PAUSED", 300, 240);
        } else if (gameState == GameState.WIN){
            parallaxBackground.setSpeed(0);
            font.setColor(Color.GOLD);
            font.getData().setScale(2);
            font.draw(spriteBatch, "YOU WIN THE GAME!", 250, 240);
        }
        spriteBatch.end();
    }

    @Override
    public void show() {
        create();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        deltaTime += Gdx.graphics.getDeltaTime();
        stage.draw();
        updateWorld();
        drawWorld();
        if (gameState == GameState.PAUSED) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            deltaTime = 0;
            stage.draw();
            updateWorld();
            drawWorld();
        }
        if (gameState == GameState.GAMEOVER){
            gameOverStage.draw();
            stage.draw();
            Gdx.input.setInputProcessor(gameOverStage);
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        gameMusic.dispose();
        shootSound.dispose();
        font.dispose();
        explosionSound.dispose();
        playerDieSound.dispose();
        projectileSound.dispose();
        spriteBatch.dispose();
        gameOverStage.dispose();
        stage.dispose();
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
    public void shootEnemyProjectile(){
        Iterator<Rectangle> iterProjectile = enemyProjectiles.iterator();
        while (iterProjectile.hasNext()) {
            Rectangle enemyProjectile = iterProjectile.next();
            enemyProjectile.x += constant.bProjSpeed * Gdx.graphics.getDeltaTime();
            enemyProjectile.y -= constant.bossGravity * Gdx.graphics.getDeltaTime();
            if ( enemyProjectile.y < 40 ){
                isActiveEnemyProjectile = false;
                iterProjectile.remove();
            }
        }
    }
}
