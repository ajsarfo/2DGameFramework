package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class IDComponent implements Component, Pool.Poolable {
    public int id;

    @Override
    public void reset() {
        id = 0;
    }
}

