package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ImageOrderComponent implements Component, Pool.Poolable {
    public float order = 0;

    @Override
    public void reset() {
        order = 0;
    }
}
