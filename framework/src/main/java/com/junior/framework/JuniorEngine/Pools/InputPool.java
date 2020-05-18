package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.InputMessage;


public class InputPool extends FlushablePool<InputMessage> {
    private static InputPool inputPool;

    private InputPool(int initialCapacity, int max){
        super(initialCapacity, max);
    }

    public static void newInstance(int initialCapacity, int maxCapacity){
        inputPool = new InputPool(initialCapacity, maxCapacity);
    }

    @Override
    protected InputMessage newObject() {
        return new InputMessage();
    }

    public static InputMessage obtainInputMessage(){
        return inputPool.obtain();
    }

    public static void freeInputMessage(InputMessage inputMessage){
        inputPool.free(inputMessage);
    }

    public static void flushPool(){
        inputPool.flush();
    }
}
