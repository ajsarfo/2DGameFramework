package com.junior.framework.JuniorEngine.Factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.junior.framework.JuniorEngine.Extensions.BodyEditorLoader;


public class BodyFactory {
    public static BodyFactory factory;
    private World world;

    public BodyFactory(World world){
        this.world = world;
    }

    //For empty body
    private Body newEmptyBody(BodyDef.BodyType type, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        Body body = world.createBody(bodyDef);
        return body;
    }

    //For circle body
    private Body newCircle(BodyDef.BodyType type, float x, float y, float radius){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    //For rectangle body
    private Body newRectangle(BodyDef.BodyType type, float x, float y, float width, float height){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f , height / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.1f;
        fixtureDef.density = 4f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    //For chain body
    private Body newChain(BodyDef.BodyType type, Vector2[] vertices, boolean loop){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        Body body = world.createBody(bodyDef);

        ChainShape shape = new ChainShape();
        if(loop) shape.createLoop(vertices);
        else shape.createChain(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    private Body newChain(BodyDef.BodyType type, Vector2[] vertices, float x, float y, boolean loop){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        ChainShape shape = new ChainShape();
        if(loop) shape.createLoop(vertices);
        else shape.createChain(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    //for making custom body
    private Body newCustomBody(String fileLocation, String name, BodyDef.BodyType type, float x, float y,  FixtureDef fixtureDef, float scale){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        Body body = world.createBody(bodyDef);

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(fileLocation));
        loader.attachFixture(body, name, fixtureDef, scale);
        return body;
    }

    private Body  newCustomBody(String fileLocation, String name, BodyDef.BodyType type, float x, float y, float scale){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.density = 1f;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(fileLocation));
        loader.attachFixture(body, name, fixtureDef, scale);
        return body;
    }

    // making the methods static
    public static void newInstance(World world){
        if(!(factory == null)) return;
        factory = new BodyFactory(world);
    }

    //creating empty body
    public static Body createEmptyBody(BodyDef.BodyType type, float x, float y){
        return factory.newEmptyBody(type, x, y);
    }

    //creating circle
    public static Body createCircle(BodyDef.BodyType type, float x, float y, float radius){
        return factory.newCircle(type, x, y, radius);
    }

    //creating rectangle
    public static Body createRectangle(BodyDef.BodyType type, float x, float y , float width, float height){
        return factory.newRectangle(type, x, y, width, height);
    }

    //creating chain body
    public static Body createChain(BodyDef.BodyType type, Vector2[] vertices, boolean loop){
        return factory.newChain(type, vertices, loop);
    }

    public static Body createChain(BodyDef.BodyType type, Vector2[] vertices, float x, float y, boolean loop){
        return factory.newChain(type, vertices, x, y, loop);
    }

    //Creating custom body
    public static Body createCustomBody(String fileLocation, String name, BodyDef.BodyType type, float x, float y,  FixtureDef fixtureDef, float scale){
        return factory.newCustomBody(fileLocation, name, type, x, y, fixtureDef, scale);
    }

    public static Body createCustomBody(String fileLocation, String name, BodyDef.BodyType type, float x, float y, float scale){
        return factory.newCustomBody(fileLocation, name, type, x, y, scale);
    }

}

