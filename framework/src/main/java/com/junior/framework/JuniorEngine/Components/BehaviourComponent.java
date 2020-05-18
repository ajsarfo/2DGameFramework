package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class BehaviourComponent implements Component, Pool.Poolable {
    public SteeringBehavior<Vector2> behavior;

    @Override
    public void reset() {
        behavior = null;
    }
}
