package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.AssetRequestMessage;


public class AssetRequestPool extends FlushablePool<AssetRequestMessage> {
    public static AssetRequestPool assetRequestPool;

    private AssetRequestPool(int initialCapacity, int max){
        super(initialCapacity, max);
    }

    @Override
    protected AssetRequestMessage newObject() {
        return new AssetRequestMessage();
    }

    public static AssetRequestMessage obtainAssetRequestMessage(){
        return  assetRequestPool.obtain();
    }

    public static void freeAssetRequestMessage(AssetRequestMessage assetRequestMessage){
        assetRequestPool.free(assetRequestMessage);
    }

    public static void flushPool(){
        assetRequestPool.flush();
    }

    public static void newInstance(int initialCapacity, int max){
        assetRequestPool = new AssetRequestPool(initialCapacity, max);
    }
}

