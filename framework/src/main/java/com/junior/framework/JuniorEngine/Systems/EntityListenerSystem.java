package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;
import com.junior.framework.JuniorEngine.Components.SteerableComponent;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;
import com.junior.framework.JuniorEngine.Internal.EntityEngine;
import com.junior.framework.JuniorEngine.Internal.GameEngine;


public class EntityListenerSystem implements EntityListener {
    private EntityEngine entityEngine;

    public EntityListenerSystem(GameEngine gameEngine){
        entityEngine = gameEngine.engine;
    }

    @Override
    public void entityAdded(Entity entity) {
        if(!(entity instanceof SteerableEntity)) return;
            entity.add(entityEngine.createComponent(SteerableComponent.class));
    }

    @Override
    public void entityRemoved(Entity entity) {
        entity.removeAll();
        if(entity instanceof BaseEntity) ((BaseEntity) entity).clearListeners();
    }
}
