package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;
import com.junior.framework.JuniorEngine.Bases.BaseScreen;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.StageMessage;
import com.junior.framework.JuniorEngine.Pools.StagePool;


import java.util.HashMap;

public class ScreenSystem extends IntervalSystem implements Telegraph {
    public GameEngine engine;

    private Viewport viewport;
    private Batch batch;
    private StateMachine<ScreenSystem, BaseScreen> screenMachine;
    private HashMap<Class, BaseScreen> screenMap;
    private Class currentScreen;

    private MessageDispatcher messageDispatcher;

    public ScreenSystem(GameEngine engine, int priority){
        super(GameEngine.FPS, priority);
        this.engine = engine;
        this.viewport = engine.viewport;
        this.batch = engine.batch;
        this.messageDispatcher = engine.messageDispatcher;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        screenMachine = new DefaultStateMachine<>(this);
        this.engine.messageDispatcher.addListener(screenMachine, Messages.INPUT);
        this.engine.messageDispatcher.addListener(this, Messages.ADD_STAGE_T0_ENTITY);
        this.engine.messageDispatcher.addListener(this, Messages.SEND_STAGE_TO_RENDER_SYSTEM);
        this.engine.messageDispatcher.addListener(this, Messages.ADD_ENTITY_TO_SCREEN);
    }

    @Override
    protected void updateInterval() {
        screenMachine.update();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(msg.message == Messages.ADD_STAGE_T0_ENTITY && (msg.sender instanceof BaseEntity)) {
            StageMessage stageMessage = StagePool.obtainStageMessage();
            stageMessage.uiStage = screenMap.get(currentScreen).getUiStage();
            stageMessage.mainStage = screenMap.get(currentScreen).getMainStage();
            messageDispatcher.dispatchMessage(this, msg.sender, Messages.ADD_STAGE_T0_ENTITY, stageMessage);
            return true;
        }

        if(msg.message == Messages.SEND_STAGE_TO_RENDER_SYSTEM && (msg.sender instanceof RenderSystem)) {
            StageMessage stageMessage = StagePool.obtainStageMessage();
            if (currentScreen != null){
                stageMessage.mainStage = screenMap.get(currentScreen).getMainStage();
                stageMessage.uiStage = screenMap.get(currentScreen).getUiStage();
            }
            messageDispatcher.dispatchMessage(this, msg.sender, Messages.SEND_STAGE_TO_RENDER_SYSTEM, stageMessage);
            return true;
        }

        if(msg.message == Messages.ADD_ENTITY_TO_SCREEN && msg.sender instanceof BaseEntity){
            screenMap.get(currentScreen).addEntity((BaseEntity) msg.sender);
            return true;
        }
        return false;
    }

    public boolean hasGlobalScreen(){
        return screenMachine.getGlobalState() != null;
    }

    public void addNewScreen(BaseScreen screen){
        if(screenMap == null) screenMap = new HashMap<>(3);
        screenMap.put(screen.getClass(), screen);
    }

    public void addNewScreen(Class screen) throws IllegalAccessException, InstantiationException {
        if(screen.getSuperclass() != BaseScreen.class) return;
        screenMap.put(screen, (BaseScreen) screen.newInstance());
    }

    public void addNewScreenAndSet(BaseScreen screen){
        addNewScreen(screen);
        changeScreen(screen.getClass());
    }

    public void addNewScreenAndSet(Class screen) throws InstantiationException, IllegalAccessException {
        addNewScreen(screen);
        changeScreen(screen);
    }

    public void changeScreen(Class screen){
        BaseScreen nextScreen = screenMap.get(screen);
        if(nextScreen == null) return;
        currentScreen = screen;
        if(nextScreen.getUiStage() == null) {
            nextScreen.addMainStage(viewport, batch);
            nextScreen.addUiStage(new ScreenViewport(new OrthographicCamera()), batch);
        };
        screenMachine.changeState(nextScreen);
    }

    public void disposeCurrentScreenAndSet(Class screen){
        Class currentScreen = this.currentScreen;
        changeScreen(screen);
        screenMap.remove(currentScreen);
    }

    public void disposeCurrentScreenAndSetNew(BaseScreen screenObject){
        Class currentScreen = this.currentScreen;
        screenMap.put(screenObject.getClass(), screenObject);
        changeScreen(screenObject.getClass());
        screenMap.remove(currentScreen);
    }

    public void disposeCurrentScreenAndSetNew(Class screen) {
        try {
            Class currentScreen = this.currentScreen;
            addNewScreen(screen);
            changeScreen(screen);
            screenMap.remove(currentScreen);
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void disposeAllExceptScreen(Class screen) {
        BaseScreen ownerScreen = screenMap.get(screen);
        screenMap.clear();
        screenMap.put(screen, ownerScreen);
    }

    //internal specific methods
    public void setScreens(HashMap<Class, BaseScreen> screenMap, Class startScreen, BaseScreen globalScreen){
        if(screenMap == null || screenMap.get(startScreen) == null || this.screenMap != null) return;
        this.screenMap = screenMap;
        screenMachine.setInitialState(new VoidScreen(startScreen));
        if(globalScreen == null) return;
        screenMachine.setGlobalState(globalScreen);
    }

    private class VoidScreen extends BaseScreen {

        private Class startScreen;

        public VoidScreen(Class startScreen){
            this.startScreen = startScreen;
        }

        @Override
        public void update(ScreenSystem screenSystem){
            screenSystem.disposeCurrentScreenAndSet(startScreen);
        }
   }
}
