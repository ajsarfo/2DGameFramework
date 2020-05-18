package com.junior.framework.JuniorEngine.Bases;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.physics.box2d.Body;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;
import com.junior.framework.JuniorEngine.Extensions.BodyFilter;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Messages.InputMessage;


import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseEntity extends SteerableEntity {

    public BaseEntity(){

    }

    public BaseEntity(GameEngine engine){
        super(engine);
    }

    public BaseEntity(GameEngine game, String nameID, Body body){
        super(game, nameID, body);
    }

    public BaseEntity(GameEngine game, String nameID){
        super(game, nameID);
    }

    public BaseEntity(GameEngine game, String nameID, BodyFilter bodyFilter){
        super(game, nameID, bodyFilter);
    }

    public BaseEntity(GameEngine game, BodyFilter bodyFilter){
        super(game, bodyFilter);
    }

    public BaseEntity(Object starter){
        super(starter);
    }

    public BaseEntity(GameEngine game, String nameID, Body body, Object starter){
        super(game, nameID, body, starter);
    }

    public BaseEntity(GameEngine game, String nameID, Object starter){
        super(game, nameID, starter);
    }

    public BaseEntity(GameEngine game, String nameID, BodyFilter bodyFilter, Object starter){
        super(game, nameID, bodyFilter, starter);
    }

    public BaseEntity(GameEngine game, BodyFilter bodyFilter, Object starter){
        super(game, bodyFilter, starter);
    }


    @Override
    protected HashMap<String, Class> requestAssets() {
        return null;
    }

    @Override
    protected void assetsResponse(HashMap<String, Object> response) {

    }

    @Override
    protected void handleInput(InputMessage message) {

    }

    @Override
    protected void handleCollision(BaseEntity entity) {

    }

    @Override
    protected void handleInteraction(ArrayList<BaseEntity> list) {

    }

    @Override
    protected boolean handleCustomMessages(Telegram telegram) {
        return false;
    }

    @Override
    protected void destroy(){
        engine.engine.removeEntity(this);
    }

    public void removeEntityFromScreen(){
        destroy();
    }
}
