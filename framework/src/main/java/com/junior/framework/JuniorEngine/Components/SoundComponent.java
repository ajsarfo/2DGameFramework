package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Extensions.SoundBundle;


import java.util.HashMap;

public class SoundComponent implements Component, Pool.Poolable {
    public HashMap<String, SoundBundle> soundMap;

    @Override
    public void reset() {
        if(soundMap != null) soundMap.clear();
    }
}
