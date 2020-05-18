package com.junior.framework.JuniorEngine.Pools;

import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.MusicMessage;


public class MusicPool extends FlushablePool<MusicMessage> {
   private static MusicPool musicPool;

   public MusicPool(int initialCapacity, int maxSize){
       super(initialCapacity, maxSize);
   }

    @Override
    protected MusicMessage newObject() {
        return new MusicMessage();
    }

   public static MusicMessage obtainMusicMessage(){
        return musicPool.obtain();
   }

   public static void freeMusicMessage(MusicMessage musicMessage){
       musicPool.free(musicMessage);
   }

   public static void flushPool(){
       musicPool.flush();
   }

   public static void newInstance(int initialCapacity, int maxSize){
       musicPool = new MusicPool(initialCapacity, maxSize);
   }
}
