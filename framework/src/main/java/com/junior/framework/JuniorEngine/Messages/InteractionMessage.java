package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;


import java.util.ArrayList;

public class InteractionMessage implements  Pool.Poolable {
    public boolean self = false;
    public ArrayList<BaseEntity> entityList = new ArrayList<>(2);

    @Override
    public void reset() {
        self = false;
        entityList.clear();
    }
}
