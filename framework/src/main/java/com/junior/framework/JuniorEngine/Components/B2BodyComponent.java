package com.junior.framework.JuniorEngine.Components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Factories.DestroyerFactory;

import java.util.ArrayList;

public class B2BodyComponent implements Component, Pool.Poolable {
    public Body body = null;
    public ArrayList<Vector2[]> polygonList;

    @Override
    public void reset() {
        polygonList = null;
        DestroyerFactory.destroyBody(body);
    }
}
