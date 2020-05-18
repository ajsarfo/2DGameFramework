package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Extensions.MusicBundle;


import java.util.HashMap;

public class MusicComponent implements Component, Pool.Poolable {
    public HashMap<String, MusicBundle> musicMap;

    @Override
    public void reset() {
        if(musicMap != null) musicMap.clear();
    }
}
