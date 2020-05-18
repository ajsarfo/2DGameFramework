package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

public class MusicMessage implements Pool.Poolable {
    public boolean query;
    public Entity entity;
    public String musicName;

    @Override
    public void reset() {
        query = false;
        entity = null;
        musicName = null;
    }
}
