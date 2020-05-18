package com.junior.framework.JuniorEngine.Pools;


import com.badlogic.gdx.utils.FlushablePool;
import com.junior.framework.JuniorEngine.Messages.InteractionMessage;

public class CollisionPool extends FlushablePool<InteractionMessage> {
    private static CollisionPool collisionPool;

    private CollisionPool(int initialCapacity, int max){
        super(initialCapacity, max);
        collisionPool = this;
    }

    public static void newInstance(int initialCapacity, int maxCapacity){
        collisionPool = new CollisionPool(initialCapacity, maxCapacity);
    }

    @Override
    protected InteractionMessage newObject() {
        return new InteractionMessage();
    }

    public static InteractionMessage obtainInteractionMessage(){
        return collisionPool.obtain();
    }

    public static void freeInteractionMessage(InteractionMessage interactionMessage){
        collisionPool.free(interactionMessage);
    }

    public static void flushPool(){
        collisionPool.flush();
    }
}
