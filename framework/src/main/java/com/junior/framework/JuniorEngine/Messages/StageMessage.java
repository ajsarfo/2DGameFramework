package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;


public class StageMessage implements Pool.Poolable {
    public BaseEntity entity;
    public Stage mainStage;
    public Stage uiStage;

    @Override
    public void reset() {
        this.mainStage = this.uiStage = null;
        entity = null;
    }
}
