package com.junior.framework.JuniorEngine.Internal;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.junior.framework.JuniorEngine.Bases.BaseScreen;
import com.junior.framework.JuniorEngine.Systems.AnimationSystem;
import com.junior.framework.JuniorEngine.Systems.AssetLoaderSystem;
import com.junior.framework.JuniorEngine.Systems.CollisionSystem;
import com.junior.framework.JuniorEngine.Systems.EntityListenerSystem;
import com.junior.framework.JuniorEngine.Systems.InputSystem;
import com.junior.framework.JuniorEngine.Systems.MusicSystem;
import com.junior.framework.JuniorEngine.Systems.RenderSystem;
import com.junior.framework.JuniorEngine.Systems.ScreenSystem;
import com.junior.framework.JuniorEngine.Systems.SoundSystem;
import com.junior.framework.JuniorEngine.Systems.UpdaterSystem;

import java.util.HashMap;

public class GameEngine {
    public World world;
    public Viewport viewport;
    public Batch batch;
    public OrthographicCamera camera;
    public EntityEngine engine;
    public MessageDispatcher messageDispatcher;

    public static  float PPM, FPS;

    public float viewportWidth, viewportHeight, framesPerSecond, worldGravity;
    public float pixelPerMeter;

    private AssetLoader assetLoader;
    private HashMap<Class, BaseScreen> screenMap;
    private Class startScreen;
    private BaseScreen globalScreen;

    public GameEngine(){
        pixelPerMeter = 100;
        viewportHeight = 270;
        viewportWidth = 270;
        framesPerSecond = 1 / 1000f;
        worldGravity = -9.8f;
    }

    private void instantiateInternals(){
        PPM = pixelPerMeter;
        FPS = framesPerSecond;
        world = new World(new Vector2(0, worldGravity), true);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        if(viewport == null) viewport = new FitViewport(viewportWidth / pixelPerMeter, viewportHeight / pixelPerMeter, camera);
        viewport.apply();
        messageDispatcher = new MessageDispatcher();
        engine = new EntityEngine();
    }

    private void instantiateSystems(){
        engine.addSystem(new AssetLoaderSystem(this, 0));
        engine.addSystem(new InputSystem(this, 1));
        engine.addSystem(new UpdaterSystem(this, 2));
        engine.addSystem(new CollisionSystem(this, 3));
        engine.addSystem(new ScreenSystem(this, 4));
        engine.addSystem(new AnimationSystem(5));
        engine.addSystem(new RenderSystem(this, 7));
        engine.addSystem(new SoundSystem(this));
        engine.addSystem(new MusicSystem(this));
        engine.addEntityListener(new EntityListenerSystem(this));
    }

    private void loadAssetsAndScreens(){
        engine.getSystem(AssetLoaderSystem.class).loadAssets(assetLoader);
        engine.getSystem(ScreenSystem.class).setScreens(screenMap, startScreen, globalScreen);
        assetLoader = null;
        screenMap = null;
        startScreen = null;
        globalScreen = null;
    }

    public void startEngine(){
        instantiateInternals();
        instantiateSystems();
        loadAssetsAndScreens();
    }

    public void updateEngine(float dt){
        engine.update(Gdx.graphics.getDeltaTime());
    }

    public void disposeEngine(){
        for(EntitySystem system : engine.getSystems()){
            engine.removeSystem(system);
        }
        world.dispose();
        batch.dispose();
    }

    public void loadAssets(AssetLoader assetLoader){
        this.assetLoader = assetLoader;
    }

    public void loadScreens(ScreenLoader screenLoader){
        if(screenLoader == null) throw new RuntimeException("screen loader not specified");
        if(screenLoader.getStartScreen() == null) throw new RuntimeException("no start screen specified");
        this.screenMap = screenLoader.getScreens();
        this.startScreen = screenLoader.getStartScreen();
        this.globalScreen = screenLoader.getGlobalScreen();
    }
}
