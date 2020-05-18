package com.junior.framework.JuniorEngine.Internal;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

public class EntityEngine extends Engine {

    public ComponentPools componentPools;

    public EntityEngine(int componentPoolInitialSize, int componentPoolMaxSize) {
        super();
        componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
    }

    public EntityEngine(){
        super();
        componentPools = new ComponentPools(1, 100);
    }

    public <T extends Component> T createComponent (Class<T> componentType) {
        return componentPools.obtain(componentType);
    }

    public void clearPools () {
        componentPools.clear();
    }

    public void freeComponent(Component component){
        if(component instanceof Pool.Poolable) componentPools.free(component);
    }

    private class ComponentPools {
        private ObjectMap<Class<?>, ReflectionPool> pools;
        private int initialSize;
        private int maxSize;

        public ComponentPools (int initialSize, int maxSize) {
            this.pools = new ObjectMap<>();
            this.initialSize = initialSize;
            this.maxSize = maxSize;
        }

        public <T> T obtain (Class<T> type) {
            ReflectionPool pool = pools.get(type);

            if (pool == null) {
                pool = new ReflectionPool(type, initialSize, maxSize);
                pools.put(type, pool);
            }

            return (T)pool.obtain();
        }

        public void free (Object object) {
            if (object == null) {
                throw new IllegalArgumentException("object cannot be null.");
            }

            ReflectionPool pool = pools.get(object.getClass());

            if (pool == null) {
                return; // Ignore freeing an object that was never retained.
            }

            pool.free(object);
        }

        public void freeAll (Array objects) {
            if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

            for (int i = 0, n = objects.size; i < n; i++) {
                Object object = objects.get(i);
                if (object == null) continue;
                free(object);
            }
        }

        public void clear () {
            for (Pool pool : pools.values()) {
                pool.clear();
            }
        }
    }
}

