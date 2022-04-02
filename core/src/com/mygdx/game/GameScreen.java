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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.objects.AirEnemy;
import com.mygdx.game.objects.GroundEnemy;
import com.mygdx.game.objects.ParallaxBackground;
import com.mygdx.game.objects.Player;
import java.util.Iterator;

public class GameScreen implements Screen {
    MyGdxGame game;

    ConstantVal constant;

    public Animation<TextureRegion> playerTexture;
    public Animation<TextureRegion> groundEnemyTexture;
    public Animation<TextureRegion> airEnemyTexture;

    public static final float SHOOT_COOLDOWN_TIME = 1f;
    private Stage stage;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    Player player;
    GroundEnemy groundEnemy;
    AirEnemy airEnemy;
    Rectangle playerBoxCollider;
    Array<Rectangle> groundEnemies;
    Array<Rectangle> projectileColliders;

    //Game clock
    float elapsedTime;
    float lastSpawnTime;

    //Sound effect in game
    Sound shootSound;
    Music gameMusic;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    public void create() {
        Gdx.app.log("GameScreen: ","menuScreen create");
        stage = new Stage();
        spriteBatch = new SpriteBatch();

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

        newGame();
    }

    private void newGame(){
        // Initialise the stateTime, aka how long the program has been running for.
        elapsedTime = 0.0f;

        constant = new ConstantVal();
        player = new Player();
        airEnemy = new AirEnemy();
        groundEnemy = new GroundEnemy();
        playerTexture = player.texture;
        airEnemyTexture = airEnemy.texture;
        groundEnemyTexture = groundEnemy.texture;

        //collider bounds
        playerBoxCollider = new Rectangle();
        playerBoxCollider.x = constant.characterX;
        playerBoxCollider.y = constant.characterY;
        playerBoxCollider.width = constant.width;
        playerBoxCollider.height = constant.height;
        groundEnemies = new Array<>();
        projectileColliders = new Array<>();
        spawnGroundEnemy();
        //spawnProjectile();

        //sound and music
        shootSound = Gdx.audio.newSound(Gdx.files.internal("projectile.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("bensoundscifi.mp3"));
        gameMusic.setLooping(true);
        gameMusic.play();
    }

    public void render(float f) {
        Gdx.app.log("GameScreen: ","gameScreen render");
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();
        camera.update();

        // The current frame to display
        TextureRegion playerCurrFrame = playerTexture.getKeyFrame(elapsedTime, true);
        TextureRegion airCurrFrame = airEnemyTexture.getKeyFrame(elapsedTime, true);
        TextureRegion groundCurrFrame = groundEnemyTexture.getKeyFrame(elapsedTime, true);

        //Apply the camera's transform to the SpriteBatch so the character is drawn in the correct position on screen.
        spriteBatch.setProjectionMatrix(camera.combined);

        stage.draw();
        spriteBatch.begin();
        spriteBatch.draw(playerCurrFrame, playerBoxCollider.x, playerBoxCollider.y, playerBoxCollider.width, playerBoxCollider.height);
        spriteBatch.draw(airCurrFrame,constant.aCharacterX, constant.aCharacterY, constant.aWidth, constant.aheight);
        for (Rectangle groundEnemy : groundEnemies){
            spriteBatch.draw(groundCurrFrame, groundEnemy.x, groundEnemy.y, groundEnemy.width, groundEnemy.height);
        }
        spriteBatch.end();

        if (Gdx.input.isTouched()) {
            if(Gdx.input.getX() < Gdx.graphics.getWidth()/3){
                if (playerBoxCollider.x > 0) {
                    playerBoxCollider.x -= 100 * Gdx.graphics.getDeltaTime();
                }
            }
            else if (Gdx.input.getX() > Gdx.graphics.getWidth()*2/3){
                if(playerBoxCollider.x + playerBoxCollider.width < 400){
                    playerBoxCollider.x += 100 * Gdx.graphics.getDeltaTime();
                }
            }
            else {
                //todo projectile!
            }
        }

        //check if time to spawn new enemy
        if(TimeUtils.nanoTime() - lastSpawnTime > 10e+9){
            spawnGroundEnemy();
        }

        Iterator<Rectangle> iter = groundEnemies.iterator();
        while (iter.hasNext()) {
            Rectangle groundEnemyBoxCollider = iter.next();
            groundEnemyBoxCollider.x -= 100 * Gdx.graphics.getDeltaTime();
            if (groundEnemyBoxCollider.x + 78 < 0)
                iter.remove();
            if (groundEnemyBoxCollider.overlaps(playerBoxCollider)) {
                iter.remove();
                //TODO player state die!!!
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
        Gdx.app.log("GameScreen: ","gameScreen show called");
        create();
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen: ","gameScreen hide called");
        Gdx.input.setInputProcessor(null);
    }

    public void spawnGroundEnemy(){
        Rectangle groundEnemyBoxCollider = new Rectangle();
        groundEnemyBoxCollider.x = constant.gCharacterX;
        groundEnemyBoxCollider.y = constant.gCharacterY;
        groundEnemyBoxCollider.width = constant.gWidth;
        groundEnemyBoxCollider.height = constant.gheight;
        groundEnemies.add(groundEnemyBoxCollider);
        lastSpawnTime = TimeUtils.nanoTime();
    }
}