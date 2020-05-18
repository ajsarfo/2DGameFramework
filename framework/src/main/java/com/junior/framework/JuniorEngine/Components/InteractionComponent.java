package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Extensions.CollisionInteraction;


import java.util.ArrayList;

public class InteractionComponent implements Component, Pool.Poolable {
    public ArrayList<CollisionInteraction> collisionInteractionList = new ArrayList<>();

    @Override
    public void reset() {
        collisionInteractionList.clear();
    }
}
