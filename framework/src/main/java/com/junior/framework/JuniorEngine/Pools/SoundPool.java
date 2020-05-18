package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.SoundMessage;


public class SoundPool extends FlushablePool<SoundMessage> {
    private static SoundPool soundPool;

    public SoundPool(int initialCapacity, int maxSize){
        super(initialCapacity, maxSize);
    }

    @Override
    protected SoundMessage newObject() {
        return new SoundMessage();
    }

    public static SoundMessage obtainSoundMessage(){
        return soundPool.obtain();
    }

    public static void freeSoundMessage(SoundMessage soundMessage){
        soundPool.free(soundMessage);
    }

    public static void flushPool(){
        soundPool.flush();
    }

    public static void newInstance(int initialCapacity, int maxSize){
        soundPool = new SoundPool(initialCapacity, maxSize);
    }
}
