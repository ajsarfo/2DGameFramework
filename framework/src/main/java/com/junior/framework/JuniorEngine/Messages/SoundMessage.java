package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

public class SoundMessage implements Pool.Poolable {
    public Entity entity;
    public String soundName;

    @Override
    public void reset() {
        entity = null;
        soundName = null;
    }
}
