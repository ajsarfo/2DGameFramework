package com.junior.framework.JuniorEngine.Factories;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class DestroyerFactory {
    private static DestroyerFactory destroyerFactory;

    private World world;

    private ArrayList<Body> bodies;
    private ArrayList<Joint> joints;

    private DestroyerFactory(World world){
        this.world = world;
        this.bodies = new ArrayList<>(1);
        this.joints = new ArrayList<>(1);
    }

    private void eliminateBody(Body body){
        this.bodies.add(body);
    }

    private void eliminateJoint(Joint joint){
        joints.add(joint);
    }

    private void empty(){
        for(Joint joint : joints) world.destroyJoint(joint);
        for(Body body : bodies) world.destroyBody(body);
        joints.clear();
        bodies.clear();
    }

    public static void newInstance(World world){
        if(destroyerFactory == null)
            destroyerFactory = new DestroyerFactory(world);
    }

    public static void destroyBody(Body body){
        destroyerFactory.eliminateBody(body);
    }

    public static void destroyJoint(Joint joint){
       destroyerFactory.eliminateJoint(joint);
    }

    public static void emptyGarbage(){
        destroyerFactory.empty();
    }
}

