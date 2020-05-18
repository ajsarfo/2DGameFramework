package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.ai.GdxAI;
import com.junior.framework.JuniorEngine.Components.SteerableComponent;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;
import com.junior.framework.JuniorEngine.Factories.BodyFactory;
import com.junior.framework.JuniorEngine.Factories.DestroyerFactory;
import com.junior.framework.JuniorEngine.Factories.JointFactory;
import com.junior.framework.JuniorEngine.Internal.GameEngine;


public class UpdaterSystem extends IntervalIteratingSystem {
    private GameEngine gameEngine;

    public UpdaterSystem(GameEngine gameEngine, int priority){
        super(Family.one(SteerableComponent.class).get(), gameEngine.framesPerSecond, priority);
        this.gameEngine = gameEngine;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        BodyFactory.newInstance(this.gameEngine.world);
        JointFactory.newInstance(this.gameEngine.world);
        DestroyerFactory.newInstance(this.gameEngine.world);
    }

    @Override
    protected void updateInterval(){
        GdxAI.getTimepiece().update(gameEngine.framesPerSecond);
        DestroyerFactory.emptyGarbage();
        gameEngine.world.step(gameEngine.framesPerSecond, 6, 2);
        super.updateInterval();
    }

    @Override
    protected void processEntity(Entity entity) {
        ((SteerableEntity) entity).update(gameEngine.framesPerSecond);
    }
}
