package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Pool;

public class FilterComponent implements Component, Pool.Poolable {
    public Filter filter;

    @Override
    public void reset() {
        filter = null;
    }
}
