package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;

public class AssetResponseMessage implements Pool.Poolable {
    public HashMap<String, Object> assetResponseMap = new HashMap<>();

    @Override
    public void reset() {
        assetResponseMap.clear();
    }
}
