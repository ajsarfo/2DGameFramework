package com.junior.framework.JuniorEngine.Bases;



import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;
import com.junior.framework.JuniorEngine.Pools.AssetRequestPool;
import com.junior.framework.JuniorEngine.Pools.AssetResponsePool;
import com.junior.framework.JuniorEngine.Pools.CollisionPool;
import com.junior.framework.JuniorEngine.Pools.InputPool;
import com.junior.framework.JuniorEngine.Pools.MusicPool;
import com.junior.framework.JuniorEngine.Pools.SoundPool;
import com.junior.framework.JuniorEngine.Pools.StagePool;
import com.junior.framework.JuniorEngine.Systems.ScreenSystem;

import java.util.ArrayList;



public abstract class BaseScreen implements State<ScreenSystem> {
    private ArrayList<BaseEntity> entityList;
    private Stage uiStage;
    private Stage mainStage;

    public BaseScreen(){
        entityList = new ArrayList<>();
    }

    @Override
    public void enter(ScreenSystem screenSystem) {
    }

    @Override
    public void update(ScreenSystem screenSystem) {

    }

    @Override
    public void exit(ScreenSystem screenSystem) {
        for(BaseEntity entity : entityList) entity.removeEntityFromScreen();
        AssetRequestPool.flushPool();
        AssetResponsePool.flushPool();
        CollisionPool.flushPool();
        InputPool.flushPool();
        MusicPool.flushPool();
        SoundPool.flushPool();
        StagePool.flushPool();
    }

    @Override
    public boolean onMessage(ScreenSystem screenSystem, Telegram telegram) {
        for (SteerableEntity entity : entityList) entity.handleMessage(telegram);
        return true;
    }

    public BaseScreen addEntity(BaseEntity entity){
        if(entityList == null) entityList = new ArrayList<>();
        entityList.add(entity);
        return this;
    }

    public ArrayList<BaseEntity> getEntityList(){
        return this.entityList;
    }

    public void addMainStage(Viewport viewport, Batch batch){
        this.mainStage = new Stage(viewport, batch);
    }

    public void addUiStage(Viewport viewport, Batch batch){
        this.uiStage = new Stage(viewport, batch);
    }

    public Stage getUiStage(){
        return this.uiStage;
    }

    public Stage getMainStage() {
        return this.mainStage;
    }
}
