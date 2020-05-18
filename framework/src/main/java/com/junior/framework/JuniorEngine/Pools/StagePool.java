package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.StageMessage;


public class StagePool extends FlushablePool<StageMessage> {
    private static StagePool stagePool;

    public StagePool(int initialCapacity , int maxSize){
        super(initialCapacity, maxSize);
    }

    @Override
    protected StageMessage newObject() {
        return new StageMessage();
    }

    public static StageMessage obtainStageMessage() {
        return stagePool.obtain();
    }

    public static void freeStageMessage(StageMessage stageMessage) {
        stagePool.free(stageMessage);
    }

    public static void flushPool() {
        stagePool.flush();
    }

    public static void newInstance(int initialCapacity, int maxSize) {
        stagePool = new StagePool(initialCapacity, maxSize);
    }
}
