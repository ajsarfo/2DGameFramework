package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;
import com.junior.framework.JuniorEngine.Components.InteractionComponent;
import com.junior.framework.JuniorEngine.Extensions.CollisionInteraction;
import com.junior.framework.JuniorEngine.Extensions.CustomB2DSprite;
import com.junior.framework.JuniorEngine.Internal.EntityEngine;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Messages.InteractionMessage;
import com.junior.framework.JuniorEngine.Pools.CollisionPool;
import com.junior.framework.JuniorEngine.Utils.Mapper;


import java.util.ArrayList;

public class CollisionSystem extends IntervalSystem implements ContactListener {
    GameEngine game;
    EntityEngine engine;
    Entity firstEntity, secondEntity;
    short firstCatBits, secondCatBits;
    MessageDispatcher messageDispatcher;

    public CollisionSystem(GameEngine game, int priority) {
        super(game.framesPerSecond, priority);
        messageDispatcher = game.messageDispatcher;
        this.game = game;
        engine = game.engine;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        game.world.setContactListener(this);
        CollisionPool.newInstance(1, 10);
    }

    @Override
    public void removedFromEngine(Engine engine){
        super.removedFromEngine(engine);
        CollisionPool.flushPool();
    }

    protected void processEntity(Entity entity) {
        if(firstEntity == null || secondEntity == null) return;

        if(entity == firstEntity || entity == secondEntity) return;

        if(!Mapper.interactionComponentMapper.has(entity)) return;

        if(!Mapper.idComponentMapper.has(entity)) return;

        ArrayList<CollisionInteraction> collisionInteractionList = Mapper.interactionComponentMapper.get(entity).collisionInteractionList;

        for(CollisionInteraction interaction : collisionInteractionList){
            if(!interaction.isSingle && interaction.equals(firstCatBits, secondCatBits)){
                InteractionMessage interactionMessage = CollisionPool.obtainInteractionMessage();
                interactionMessage.entityList.add((BaseEntity) firstEntity);
                interactionMessage.entityList.add((BaseEntity) secondEntity);
                messageDispatcher.dispatchMessage( Mapper.idComponentMapper.get(entity).id, interactionMessage);
                return;
            }

            if(interaction.isSingle && interaction.equals(firstCatBits)){
                InteractionMessage interactionMessage = CollisionPool.obtainInteractionMessage();
                interactionMessage.entityList.add((BaseEntity) firstEntity);
                messageDispatcher.dispatchMessage( Mapper.idComponentMapper.get(entity).id, interactionMessage);
                return;
            }

            if(interaction.isSingle && interaction.equals(secondCatBits)){
                InteractionMessage interactionMessage =  CollisionPool.obtainInteractionMessage();
                interactionMessage.entityList.add((BaseEntity) secondEntity);
                messageDispatcher.dispatchMessage( Mapper.idComponentMapper.get(entity).id, interactionMessage);
                return;
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        //First entity
        if(contact.getFixtureA().getBody().getUserData() instanceof Entity)
            firstEntity = (Entity) contact.getFixtureA().getBody().getUserData();

        else if(contact.getFixtureA().getBody().getUserData() instanceof CustomB2DSprite)
            firstEntity = (Entity) ((CustomB2DSprite) contact.getFixtureA().getBody().getUserData()).getUserData();

        //Second entity
        if(contact.getFixtureB().getBody().getUserData() instanceof Entity)
            secondEntity = (Entity) contact.getFixtureB().getBody().getUserData();

        else if(contact.getFixtureB().getBody().getUserData() instanceof CustomB2DSprite)
            secondEntity = (Entity) ((CustomB2DSprite) contact.getFixtureB().getBody().getUserData()).getUserData();

        if(firstEntity == null || secondEntity == null) return;

        firstCatBits = contact.getFixtureA().getFilterData().categoryBits;
        secondCatBits = contact.getFixtureB().getFilterData().categoryBits;

        if(Mapper.idComponentMapper.has(firstEntity)){
            InteractionMessage entityMessage = CollisionPool.obtainInteractionMessage();
            entityMessage.self = true;
            entityMessage.entityList.add((BaseEntity) secondEntity);
            messageDispatcher.dispatchMessage(Mapper.idComponentMapper.get(firstEntity).id, entityMessage);
        }

        if(Mapper.idComponentMapper.has(secondEntity)){
            InteractionMessage entityMessage = CollisionPool.obtainInteractionMessage();
            entityMessage.self = true;
            entityMessage.entityList.add((BaseEntity) firstEntity);
            messageDispatcher.dispatchMessage(Mapper.idComponentMapper.get(secondEntity).id, entityMessage);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    protected void updateInterval() {
        for(Entity entity : engine.getEntitiesFor(Family.one(InteractionComponent.class).get()))
            processEntity(entity);
        firstEntity = secondEntity = null;
    }
}
