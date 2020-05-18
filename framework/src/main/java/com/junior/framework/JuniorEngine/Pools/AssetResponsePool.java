package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.AssetResponseMessage;


public class AssetResponsePool extends FlushablePool<AssetResponseMessage> {
    public static AssetResponsePool assetResponsePool;

    private AssetResponsePool(int initialCapacity, int max){
        super(initialCapacity, max);
    }

    @Override
    protected AssetResponseMessage newObject() {
        return new AssetResponseMessage();
    }

    public static AssetResponseMessage obtainAssetResponseMessage(){
        return  assetResponsePool.obtain();
    }

    public static void freeAssetResponseMessage(AssetResponseMessage assetResponseMessage){
        assetResponsePool.free(assetResponseMessage);
    }

    public static void flushPool(){
        assetResponsePool.flush();
    }

    public static void newInstance(int initialCapacity, int max){
        assetResponsePool = new AssetResponsePool(initialCapacity, max);
    }
}
