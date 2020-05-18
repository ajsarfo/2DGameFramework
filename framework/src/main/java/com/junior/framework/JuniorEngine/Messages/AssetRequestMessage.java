package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;

public class AssetRequestMessage implements Pool.Poolable {
    public HashMap<String, Class> assetMap = null;

    @Override
    public void reset() {
        assetMap = null;
    }
}