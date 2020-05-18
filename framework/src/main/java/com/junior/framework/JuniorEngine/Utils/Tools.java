package com.junior.framework.JuniorEngine.Utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;
import com.junior.framework.JuniorEngine.Components.AnimationComponent;
import com.junior.framework.JuniorEngine.Components.B2BodyComponent;
import com.junior.framework.JuniorEngine.Components.BehaviourComponent;
import com.junior.framework.JuniorEngine.Components.FilterComponent;
import com.junior.framework.JuniorEngine.Components.IDComponent;
import com.junior.framework.JuniorEngine.Components.ImageOrderComponent;
import com.junior.framework.JuniorEngine.Components.InteractionComponent;
import com.junior.framework.JuniorEngine.Components.MusicComponent;
import com.junior.framework.JuniorEngine.Components.SoundComponent;
import com.junior.framework.JuniorEngine.Entities.SteerableEntity;
import com.junior.framework.JuniorEngine.Extensions.BodyEditorLoader;
import com.junior.framework.JuniorEngine.Extensions.CollisionInteraction;
import com.junior.framework.JuniorEngine.Extensions.CustomAnimation;
import com.junior.framework.JuniorEngine.Extensions.CustomB2DSprite;
import com.junior.framework.JuniorEngine.Extensions.MusicBundle;
import com.junior.framework.JuniorEngine.Extensions.ShapeTextureBundle;
import com.junior.framework.JuniorEngine.Extensions.ShapeTextureBundleAnimation;
import com.junior.framework.JuniorEngine.Extensions.SoundBundle;
import com.junior.framework.JuniorEngine.Extensions.TextureBundle;
import com.junior.framework.JuniorEngine.Extensions.TextureBundleAnimation;
import com.junior.framework.JuniorEngine.Internal.EntityEngine;
import com.junior.framework.JuniorEngine.Internal.GameEngine;


import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import java.util.HashMap;

public abstract class Tools {

    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

    public static float angleToRad(float angle) {
        return (float) Math.toRadians(angle);
    }

    public static float radToAngle(float rad) {
        return (float) Math.toDegrees(rad);
    }

    public static int stringToID(String string) {
        return string.hashCode();
    }

    public static Body rotateBody(Body body, float angle){
        body.setTransform(0, 0, Tools.angleToRad(angle));
        return body;
    }

    //Entity specific
    public static void addInteraction(EntityEngine engine, Entity entity, short singleInteraction) {
        if (!Mapper.interactionComponentMapper.has(entity))
            entity.add(engine.createComponent(InteractionComponent.class));
        CollisionInteraction collisionInteraction = new CollisionInteraction(singleInteraction);
        for (CollisionInteraction inter :Mapper.interactionComponentMapper.get(entity).collisionInteractionList)
            if (inter.equals(collisionInteraction)) return;
        Mapper.interactionComponentMapper.get(entity).collisionInteractionList.add(collisionInteraction);

    }

    public static void addInteraction(EntityEngine engine, Entity entity, short firstInteraction, short secondInteraction) {
        if (!Mapper.interactionComponentMapper.has(entity))
            entity.add(engine.createComponent(InteractionComponent.class));
        CollisionInteraction collisionInteraction = new CollisionInteraction(firstInteraction, secondInteraction);
        for (CollisionInteraction inter : Mapper.interactionComponentMapper.get(entity).collisionInteractionList)
            if (inter.equals(collisionInteraction)) return;
        Mapper.interactionComponentMapper.get(entity).collisionInteractionList.add(collisionInteraction);
    }

    public static int addID(EntityEngine engine, Entity entity, String nameID) {
        IDComponent idComponent = engine.createComponent(IDComponent.class);
        idComponent.id = nameID.hashCode();
        entity.add(idComponent);
        return idComponent.id;
    }

    public static Body addBody(EntityEngine engine, Entity entity, Body body) {
        B2BodyComponent b2BodyComponent = engine.createComponent(B2BodyComponent.class);
        b2BodyComponent.body = body;
        entity.add(b2BodyComponent);
        body.setUserData(entity);

        if(Mapper.filterComponentMapper.has(entity) && !(Mapper.filterComponentMapper.get(entity).filter == null)){
            for(Fixture fixture : body.getFixtureList())
                fixture.setFilterData(Mapper.filterComponentMapper.get(entity).filter);
        }

        return body;
    }

    public static  void addFilter(EntityEngine engine, Entity entity, short catBits){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("Failed to add filter: either body is null, component on " + entity + " is not set or body component was added before filter component");

        Filter filter = new Filter();
        filter.categoryBits = catBits;
        for(Fixture fixture : Mapper.b2BodyComponentMapper.get(entity).body.getFixtureList()){
            fixture.setFilterData(filter);
        }

        FilterComponent filterComponent = engine.createComponent(FilterComponent.class);
        filterComponent.filter = filter;
        entity.add(filterComponent);
    }

    public static void addFilter(EntityEngine engine, Entity entity, short catBits, short maskBits){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("Failed to add filter: either body is null, component on " + entity + " is not set or body component was added before filter component");
        Filter filter = new Filter();
        filter.categoryBits = catBits;
        filter.maskBits = maskBits;

        for(Fixture fixture : Mapper.b2BodyComponentMapper.get(entity).body.getFixtureList()){
            fixture.setFilterData(filter);
        }

        FilterComponent filterComponent = engine.createComponent(FilterComponent.class);
        filterComponent.filter = filter;
        entity.add(filterComponent);
    }

    public static void addFilter(EntityEngine engine, Entity entity, short catBits, short maskBits, short group){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("Failed to add filter: either body is null, component on " + entity + " is not set or body component was added before filter component");
        Filter filter = new Filter();
        filter.categoryBits = catBits;
        filter.maskBits = maskBits;
        filter.groupIndex = group;

        for(Fixture fixture : Mapper.b2BodyComponentMapper.get(entity).body.getFixtureList()){
            fixture.setFilterData(filter);
        }

        FilterComponent filterComponent = engine.createComponent(FilterComponent.class);
        filterComponent.filter = filter;
        entity.add(filterComponent);
    }

    public static void addFilter(EntityEngine engine, Entity entity, Filter filter){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("Failed to add filter: either body is null, component on " + entity + " is not set or body component was added before filter component");

        for(Fixture fixture : Mapper.b2BodyComponentMapper.get(entity).body.getFixtureList()){
            fixture.setFilterData(filter);
        }

        FilterComponent filterComponent = engine.createComponent(FilterComponent.class);
        filterComponent.filter = filter;
        entity.add(filterComponent);
    }

    public static void addBehaviour(EntityEngine engine, Entity entity, SteeringBehavior<Vector2> behavior) {
        BehaviourComponent behaviourComponent = engine.createComponent(BehaviourComponent.class);
        behaviourComponent.behavior = behavior;
        entity.add(behaviourComponent);
    }

    public static void addSound(EntityEngine engine, Entity entity, MessageDispatcher messageDispatcher, Sound sound, String soundName){
        if(!Mapper.soundComponentMapper.has(entity))
            entity.add(engine.createComponent(SoundComponent.class));

        HashMap<String, SoundBundle> soundMap = Mapper.soundComponentMapper.get(entity).soundMap;
        if(Mapper.soundComponentMapper.get(entity).soundMap == null) soundMap = new HashMap<>();
        soundMap.put(soundName, new SoundBundle(sound, soundName, messageDispatcher, entity));
        Mapper.soundComponentMapper.get(entity).soundMap =  soundMap;
    }

    public static void addMusic(EntityEngine engine, Entity entity, MessageDispatcher messageDispatcher, Music music, String musicName){
        if(!Mapper.soundComponentMapper.has(entity))
            entity.add(engine.createComponent(MusicComponent.class));

        HashMap<String, MusicBundle> musicMap = Mapper.musicComponentMapper.get(entity).musicMap;
        if(Mapper.musicComponentMapper.get(entity).musicMap == null) musicMap = new HashMap<>();
        musicMap.put(musicName, new MusicBundle(music, musicName, messageDispatcher, entity));
        Mapper.musicComponentMapper.get(entity).musicMap =  musicMap;
    }

    public static SoundBundle getSound(Entity entity, String soundName){
        if(!Mapper.soundComponentMapper.has(entity)) return null;
        return Mapper.soundComponentMapper.get(entity).soundMap.get(soundName);
    }

    public static MusicBundle getMusic(Entity entity, String musicName){
        if(!Mapper.musicComponentMapper.has(entity)) return null;
        return Mapper.musicComponentMapper.get(entity).musicMap.get(musicName);
    }

    public static void removeSound(Entity entity, String soundName){
        if(!Mapper.soundComponentMapper.has(entity)) return;
        Mapper.soundComponentMapper.get(entity).soundMap.remove(soundName);
    }

    public static void removeMusic(Entity entity, String musicName){
        if(!Mapper.musicComponentMapper.has(entity)) return;
        Mapper.musicComponentMapper.get(entity).musicMap.remove(musicName);
    }

    public static void addTextureToBodyFixture(TextureRegion textureRegion, Body body) {
        if (body == null || body.getFixtureList().isEmpty()) return;
        body.getFixtureList().get(0).setUserData(new Box2DSprite(textureRegion));
    }

    public static void addTextureToBodyFixture(TextureRegion textureRegion, Body body, int order) {
        if (body == null || body.getFixtureList().isEmpty()) return;
        Box2DSprite box2DSprite = new Box2DSprite(textureRegion);
        box2DSprite.setZIndex(order);
        body.getFixtureList().get(0).setUserData(box2DSprite);
    }

    public static void addTextureToBodyFixture(Texture texture, Body body) {
        if (body == null || body.getFixtureList().isEmpty()) return;
        body.getFixtureList().get(0).setUserData(new Box2DSprite(texture));
    }

    public static void addTextureToBodyFixture(Texture texture, Body body, int order) {
        if (body == null || body.getFixtureList().isEmpty()) return;
        Box2DSprite box2DSprite = new Box2DSprite(texture);
        box2DSprite.setZIndex(order);
        body.getFixtureList().get(0).setUserData(box2DSprite);
    }

    public static ShapeTextureBundleAnimation loadAnimation(String animationShapesLocation, String animationName, TextureRegion animationImage, int rowNum, int colNum, float duration, boolean loop) {
        BodyEditorLoader loader = new BodyEditorLoader(animationShapesLocation);
        Array<Array<Vector2[]>> animationShapes = loader.retrieveAnimationShapes(animationName);

        if (!((rowNum * colNum) == animationShapes.size))
            throw new RuntimeException("animation tile number not equal to animation shapes");

        int tileWidth = animationImage.getRegionWidth() / colNum;
        int tileHeight = animationImage.getRegionHeight() / rowNum;
        TextureRegion[][] spritesAnimation = animationImage.split(tileWidth, tileHeight);

        ShapeTextureBundle[] shapeTextureBundleArray = new ShapeTextureBundle[rowNum * colNum];

        int iter = 0;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                shapeTextureBundleArray[iter] = new ShapeTextureBundle(animationShapes.get(iter), spritesAnimation[i][j], (iter));
                iter++;
            }
        }
        ShapeTextureBundleAnimation shapeTextureBundleAnimation = new ShapeTextureBundleAnimation(shapeTextureBundleArray, duration, loop);
        return shapeTextureBundleAnimation;
    }

    public static TextureBundleAnimation loadAnimation(TextureRegion animationTextureSheet, int rowNum, int colNum, float duration, boolean loop){
        int tileWidth = animationTextureSheet.getRegionWidth() / colNum;
        int tileHeight = animationTextureSheet.getRegionHeight() / rowNum;
        TextureRegion[][] spritesAnimation = animationTextureSheet.split(tileWidth, tileHeight);

        TextureBundle[] shapeTextureBundleArray = new TextureBundle[rowNum * colNum];

        int iter = 0;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                shapeTextureBundleArray[iter] = new TextureBundle(spritesAnimation[i][j], (iter));
                iter++;
            }
        }

        TextureBundleAnimation textureBundleAnimation = new TextureBundleAnimation(shapeTextureBundleArray, duration, loop);
        return textureBundleAnimation;
    }

    public static void addAnimation(EntityEngine engine, Entity entity, ShapeTextureBundleAnimation animation){
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        animationComponent.isTextureAnimation = false;
        animationComponent.shapeTextureAnimation = animation;
        entity.add(animationComponent);
        animation.reset();
        animation.play();
        CustomB2DSprite customB2DSprite = new CustomB2DSprite();
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("failed to add animation to entity: either body is null or body component is not added to entity");
       Mapper.b2BodyComponentMapper.get(entity).body.setUserData(customB2DSprite);
        attachShapeAndSpriteToEntity(entity, animation.getCurrentBundle());
    }

    public static void addAnimation(EntityEngine engine, Entity entity, TextureBundleAnimation animation){
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        animationComponent.textureAnimation = animation;
        entity.add(animationComponent);
        animation.reset();
        animation.play();
        CustomB2DSprite customB2DSprite = new CustomB2DSprite();
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null)
            throw new RuntimeException("failed to add animation to entity: either body is null or body component is not added to entity");
        Mapper.b2BodyComponentMapper.get(entity).body.setUserData(customB2DSprite);
        Mapper.b2BodyComponentMapper.get(entity).body.setActive(false);
        attachSpriteToEntity(entity, animation.getCurrentBundle());
    }

    public static CustomAnimation getAnimation(Entity entity){
        if(!Mapper.animationComponentMapper.has(entity)) return null;
        AnimationComponent component = Mapper.animationComponentMapper.get(entity);
        if(component.isTextureAnimation) return component.textureAnimation;
        return component.shapeTextureAnimation;
    }

    public static void attachSpriteToEntity(Entity entity, TextureBundle textureBundle){
        if(!Mapper.b2BodyComponentMapper.has(entity) || (Mapper.b2BodyComponentMapper.get(entity).body == null)) return;;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;

        PolygonShape polygonShape = new PolygonShape();

        if(body.getFixtureList().isEmpty()) {
            FixtureDef fixtureDef = new FixtureDef();
            polygonShape.setAsBox(textureBundle.getSprite().getRegionWidth() / GameEngine.PPM / 2f, textureBundle.getSprite().getRegionHeight() / GameEngine.PPM / 2f);
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
        }

        else{
            body.destroyFixture(body.getFixtureList().get(0));
            FixtureDef fixtureDef = new FixtureDef();
            polygonShape.setAsBox(textureBundle.getSprite().getRegionWidth() / GameEngine.PPM / 2f, textureBundle.getSprite().getRegionHeight() / GameEngine.PPM / 2f);
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
        }

        polygonShape.dispose();

        CustomB2DSprite box2DSprite = (CustomB2DSprite) body.getUserData();
        box2DSprite.setRegion(textureBundle.getSprite());
        box2DSprite.setIndex(textureBundle.getIndex());
        box2DSprite.setUserData(entity);
        body.setUserData(box2DSprite);

        if(!Mapper.imageOrderComponentMapper.has(entity) || Mapper.imageOrderComponentMapper.get(entity).order == box2DSprite.getZIndex()) return;

        box2DSprite.setZIndex(Mapper.imageOrderComponentMapper.get(entity).order);
    }

    public static void attachShapeAndSpriteToEntity(Entity entity, ShapeTextureBundle shapeTextureBundle) {
        if(!Mapper.b2BodyComponentMapper.has(entity) && (Mapper.b2BodyComponentMapper.get(entity).body == null)) return;;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();

        if(body.getFixtureList().isEmpty()){
            for(Vector2[] fixtureShape : shapeTextureBundle.getShape()){
                polygonShape.set(fixtureShape);
                fixtureDef.shape = polygonShape;

                if(Mapper.filterComponentMapper.has(entity)){
                    Filter filter = Mapper.filterComponentMapper.get(entity).filter;
                    if(!(filter == null)){
                        fixtureDef.filter.categoryBits = filter.categoryBits;
                        fixtureDef.filter.maskBits = filter.maskBits;
                        fixtureDef.filter.groupIndex = filter.groupIndex;
                    }
                }

                body.createFixture(fixtureDef);
            }
        }

        else{
            Fixture fixture = body.getFixtureList().get(0);
            Filter filterData= fixture.getFilterData();
            float density = fixture.getDensity();
            float friction = fixture.getFriction();
            float restitution = fixture.getRestitution();
            boolean isSensor = fixture.isSensor();

            for(Fixture fixture1 : body.getFixtureList())
                body.destroyFixture(fixture1);


            fixtureDef.filter.maskBits = filterData.maskBits;
            fixtureDef.filter.categoryBits = filterData.categoryBits;
            fixtureDef.filter.groupIndex = filterData.groupIndex;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            fixtureDef.isSensor = isSensor;

            for(Vector2[] fixtureShape : shapeTextureBundle.getShape()){
                polygonShape.set(fixtureShape);
                fixtureDef.shape = polygonShape;
                body.createFixture(fixtureDef);
            }
        }

        polygonShape.dispose();

        CustomB2DSprite box2DSprite = (CustomB2DSprite) body.getUserData();
        box2DSprite.setRegion(shapeTextureBundle.getSprite());
        box2DSprite.setIndex(shapeTextureBundle.getIndex());
        box2DSprite.setUserData(entity);
        if(Mapper.imageOrderComponentMapper.has(entity) && !(Mapper.imageOrderComponentMapper.get(entity).order == box2DSprite.getZIndex()))
            box2DSprite.setZIndex(Mapper.imageOrderComponentMapper.get(entity).order);
        body.setUserData(box2DSprite);
    }

    public static void scaleAnimation(TextureBundleAnimation animation, float scale){
        for(TextureBundle bundle : animation.getFrames())
            bundle.scale(scale);
    }

    public static void addImageOrder(EntityEngine engine, Entity entity, float order){
        ImageOrderComponent imageOrderComponent = engine.createComponent(ImageOrderComponent.class);
        imageOrderComponent.order = order;
        entity.add(imageOrderComponent);
    }

    public static void scaleEntity(Entity entity, float scale) {
        if(!Mapper.b2BodyComponentMapper.has(entity)) return;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;

        if (body == null || body.getFixtureList().isEmpty()) return;

        if(body.getUserData() == null || !(body.getUserData() instanceof CustomB2DSprite)) return;

        if(Mapper.animationComponentMapper.has(entity) && !Mapper.animationComponentMapper.get(entity).isTextureAnimation){
            ShapeTextureBundleAnimation animation = Mapper.animationComponentMapper.get(entity).shapeTextureAnimation;
            return;
        }

        CustomB2DSprite sprite = (CustomB2DSprite) body.getUserData();
        sprite.scale(scale);
    }

    public static void centerAtPosition(BaseEntity entity, float x, float y){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null) return;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;
        body.setTransform(x, y, 0);
    }

    public static void centerAtPosition(Body body, float x, float y){
        if(body == null) return;
        body.setTransform(x, y, 0);
    }

    public static void moveBy(BaseEntity entity, float x, float y){
        if(!Mapper.b2BodyComponentMapper.has(entity) || Mapper.b2BodyComponentMapper.get(entity).body == null) return;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;
        body.setTransform(body.getPosition().x + x, body.getPosition().y + y, 0);
    }

    public static void moveBy(Body body, float x, float y){
        if(body == null) return;
        body.setTransform(body.getPosition().x + x, body.getPosition().y + y, 0);
    }

    public static Box2DSprite  getSprite(SteerableEntity entity){
        if(!Mapper.b2BodyComponentMapper.has(entity)) return null;
        return getSprite(Mapper.b2BodyComponentMapper.get(entity).body);
    }

    public static Box2DSprite getSprite(Body body){
        if(body.getUserData() instanceof Box2DSprite) return (Box2DSprite) body.getUserData();
        return (Box2DSprite) body.getFixtureList().get(0).getUserData();
    }
}
