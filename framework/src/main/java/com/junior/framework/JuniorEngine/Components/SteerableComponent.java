package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;


public class SteerableComponent implements Component, Pool.Poolable {
    public SteerableEntity steerable;

    @Override
    public void reset() {
    }
}
