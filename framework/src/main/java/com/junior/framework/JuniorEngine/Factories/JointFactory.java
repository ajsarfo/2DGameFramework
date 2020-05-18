package com.junior.framework.JuniorEngine.Factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class JointFactory {
    private static JointFactory factory;
    private World world;

    public JointFactory(World world){
        this.world = world;
    }

    private DistanceJoint newDistanceJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, float length){
        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;
        jointDef.localAnchorA.set(anchorA);
        jointDef.localAnchorB.set(anchorB);
        jointDef.length = length;
        jointDef.collideConnected = false;
        return (DistanceJoint) world.createJoint(jointDef);
    }

    private RevoluteJoint newRevoluteJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor){
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;
        jointDef.localAnchorA.set(anchorA);
        jointDef.localAnchorB.set(anchorB);
        jointDef.collideConnected= false;
        jointDef.enableMotor = enableMotor;
        jointDef.referenceAngle = bodyB.getAngle() - bodyA.getAngle();
        jointDef.enableLimit = true;

        return (RevoluteJoint) world.createJoint(jointDef);
    }

    private PrismaticJoint newPrismaticJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor){
        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;
        jointDef.localAnchorA.set(anchorA);
        jointDef.localAnchorB.set(anchorB);
        jointDef.localAxisA.set(new Vector2(0, 1));
        jointDef.collideConnected = false;
        jointDef.referenceAngle = bodyB.getAngle() - bodyA.getAngle();
        jointDef.enableMotor = enableMotor;
        jointDef.enableLimit = true;

        return (PrismaticJoint) world.createJoint(jointDef);
    }

    private PrismaticJoint newPrismaticJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor, float angle){
        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;
        jointDef.localAnchorA.set(anchorA);
        jointDef.localAnchorB.set(anchorB);
        jointDef.localAxisA.set(new Vector2(-MathUtils.sinDeg(angle), MathUtils.cosDeg(angle)));
        jointDef.collideConnected = false;
        jointDef.referenceAngle = 0;
        jointDef.enableMotor = enableMotor;
        jointDef.enableLimit = true;

        return (PrismaticJoint) world.createJoint(jointDef);
    }

    private WeldJoint newWeldJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB){
        WeldJointDef joint = new WeldJointDef();
        joint.bodyA = bodyA;
        joint.bodyB = bodyB;
        joint.localAnchorA.set(anchorA);
        joint.localAnchorB.set(anchorB);
        joint.collideConnected = false;
        joint.referenceAngle = bodyB.getAngle() - bodyA.getAngle();
        return (WeldJoint) world.createJoint(joint);
    }

    public static DistanceJoint createDistanceJoint(Body bodyA, Body bodyB, Vector2 anchorA ,Vector2 anchorB, float length){
        return factory.newDistanceJoint(bodyA, bodyB, anchorA, anchorB, length);
    }

    public static RevoluteJoint createRevoluteJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor){
        return factory.newRevoluteJoint(bodyA, bodyB, anchorA, anchorB, enableMotor);
    }

    public static PrismaticJoint createPrismaticJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor){
        return factory.newPrismaticJoint(bodyA, bodyB, anchorA, anchorB, enableMotor);
    }

    public static PrismaticJoint createPrismaticJoint(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB, boolean enableMotor, float angle){
        return factory.newPrismaticJoint(bodyA, bodyB, anchorA, anchorB, enableMotor, angle);
    }

    public static WeldJoint createWeldJoint(Body bodyA, Body bodyB, Vector2 anchorA , Vector2 anchorB){
        return factory.newWeldJoint(bodyA, bodyB, anchorA, anchorB);
    }

    public static void newInstance(World world){
        if(!(factory == null)) return;
        factory = new JointFactory(world);
    }
}
