package com.junior.framework.JuniorEngine.Entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.junior.framework.JuniorEngine.Bases.BaseEntity;
import com.junior.framework.JuniorEngine.Extensions.BodyFilter;
import com.junior.framework.JuniorEngine.Extensions.CollisionInteraction;
import com.junior.framework.JuniorEngine.Extensions.CustomAnimation;
import com.junior.framework.JuniorEngine.Extensions.MusicBundle;
import com.junior.framework.JuniorEngine.Extensions.ShapeTextureBundleAnimation;
import com.junior.framework.JuniorEngine.Extensions.SoundBundle;
import com.junior.framework.JuniorEngine.Extensions.TextureBundleAnimation;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.AssetRequestMessage;
import com.junior.framework.JuniorEngine.Messages.AssetResponseMessage;
import com.junior.framework.JuniorEngine.Messages.InputMessage;
import com.junior.framework.JuniorEngine.Messages.InteractionMessage;
import com.junior.framework.JuniorEngine.Messages.StageMessage;
import com.junior.framework.JuniorEngine.Pools.AssetRequestPool;
import com.junior.framework.JuniorEngine.Pools.AssetResponsePool;
import com.junior.framework.JuniorEngine.Pools.CollisionPool;
import com.junior.framework.JuniorEngine.Pools.StagePool;
import com.junior.framework.JuniorEngine.Systems.ScreenSystem;
import com.junior.framework.JuniorEngine.Utils.Mapper;
import com.junior.framework.JuniorEngine.Utils.Tools;


import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SteerableEntity extends Entity implements Telegraph, Steerable<Vector2> {
    protected Object starter;
    protected GameEngine engine;
    protected Body body;
    protected Batch batch;
    protected Stage uiStage, mainStage;

    private float maxLinearSpeed = 2f;
    private float maxLinearAcceleration = 5f;
    private float maxAngularSpeed = 50f;
    private float maxAngularAcceleration = 5f;
    private float zeroThreshold = 0.1f;

    private float boundingRadius = 1f;
    private boolean isTagged = true;
    private boolean independentFacing = false;

    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());

    public SteerableEntity(){
    }

    public SteerableEntity(GameEngine engine){
        this.engine = engine;
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, String nameID){
        this.engine = engine;
        this.setId(nameID);
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, String nameID, Body body){
        this.engine = engine;
        this.setId(nameID);
        this.setBody(body);
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, BodyFilter bodyFilter){
        this.engine = engine;
        if(bodyFilter.hasBody()) this.setBody(bodyFilter.getBody());
        if(bodyFilter.hasFilter()) setFilter(bodyFilter.getFilter());
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, String nameID, BodyFilter bodyFilter){
        this.engine = engine;
        this.setId(nameID);
        if(bodyFilter.hasBody()) this.setBody(bodyFilter.getBody());
        if(bodyFilter.hasFilter()) this.setFilter(bodyFilter.getFilter());
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(Object starter){
        this.starter = starter;
    }

    public SteerableEntity(GameEngine engine, String nameID, Object starter){
        this.engine = engine;
        this.setId(nameID);
        this.starter = starter;
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, String nameID, Body body, Object starter){
        this.engine = engine;
        this.setId(nameID);
        this.setBody(body);
        this.starter = starter;
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, BodyFilter bodyFilter, Object starter){
        this.engine = engine;
        if(bodyFilter.hasBody()) this.setBody(bodyFilter.getBody());
        if(bodyFilter.hasFilter()) setFilter(bodyFilter.getFilter());
        this.starter = starter;
        loadAndStart();
        engine.engine.addEntity(this);
    }

    public SteerableEntity(GameEngine engine, String nameID, BodyFilter bodyFilter, Object starter){
        this.engine = engine;
        this.setId(nameID);
        if(bodyFilter.hasBody()) this.setBody(bodyFilter.getBody());
        if(bodyFilter.hasFilter()) this.setFilter(bodyFilter.getFilter());
        this.starter = starter;
        loadAndStart();
        engine.engine.addEntity(this);
    }

    @Override
    public Component remove (Class<? extends Component> componentClass) {
        Component component = super.remove(componentClass);
        if (component != null) engine.engine.freeComponent(component);
        return component;
    }

    public void update(float dt){
        process();
        if(!Mapper.behaviourComponentMapper.has(this) || !Mapper.b2BodyComponentMapper.has(this)) return;
        if(Mapper.behaviourComponentMapper.get(this).behavior == null || Mapper.b2BodyComponentMapper.get(this).body == null) return;
        Mapper.behaviourComponentMapper.get(this).behavior.calculateSteering(steeringOutput);
        applySteering(steeringOutput, dt);
    }

    public void clearListeners(){
        engine.messageDispatcher.removeListener(this, Messages.ADD_STAGE_T0_ENTITY);
        engine.messageDispatcher.removeListener(this, Messages.RECEIVE_ASSET);
        if(this.hasID()) engine.messageDispatcher.removeListener(this, getId());
    }

    protected void applySteering(SteeringAcceleration<Vector2> steeringOutput, float dt){
        boolean anyAcceleration = false;

        if(!steeringOutput.linear.isZero()){
            Mapper.b2BodyComponentMapper.get(this).body.applyForceToCenter(steeringOutput.linear, true);
            anyAcceleration = true;
        }

        if(isIndependentFacing()){
            if(steeringOutput.angular != 0){
                Mapper.b2BodyComponentMapper.get(this).body.applyTorque(steeringOutput.angular, true);
                anyAcceleration = true;
            }
        }
        else{
            Vector2 linVel = getLinearVelocity();
            if(!linVel.isZero(getZeroLinearSpeedThreshold())){
                float newOrientation = vectorToAngle(linVel);
                Mapper.b2BodyComponentMapper.get(this).body.setAngularVelocity((newOrientation - getAngularVelocity()) * dt);
                Mapper.b2BodyComponentMapper.get(this).body.setTransform(Mapper.b2BodyComponentMapper.get(this).body.getPosition(), newOrientation);
            }
        }

        if(anyAcceleration){
            Vector2 velocity = Mapper.b2BodyComponentMapper.get(this).body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if(currentSpeedSquare > (maxLinearSpeed * maxLinearSpeed)){
                Mapper.b2BodyComponentMapper.get(this).body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }
            //cap the angular speed
            float maxAngVelocity = getMaxAngularSpeed();
            if(Mapper.b2BodyComponentMapper.get(this).body.getAngularVelocity() > maxAngVelocity)
                Mapper.b2BodyComponentMapper.get(this).body.setAngularVelocity(maxAngVelocity);
        }
    }

    protected void handleInteraction(InteractionMessage data){
        int i = 0;
        if(data.self) {
            handleCollision(data.entityList.get(0));
        }
        else {
            System.out.println(this + " " + "other " + i++ +" : " + data.entityList);
            handleInteraction(data.entityList);
        }
    }

    private void loadAndStart(){
        //Loading assets for entity
        HashMap<String, Class> assetRequestMap = (this).requestAssets();
        if (!(assetRequestMap == null) && !assetRequestMap.isEmpty()) {
            AssetRequestMessage assetRequestMessage = AssetRequestPool.obtainAssetRequestMessage();
            assetRequestMessage.assetMap = assetRequestMap;
            engine.messageDispatcher.addListener(this, Messages.RECEIVE_ASSET);
            engine.messageDispatcher.dispatchMessage(this, Messages.RETRIEVE_ASSET, assetRequestMessage);
        }
        engine.messageDispatcher.addListener(this, Messages.ADD_STAGE_T0_ENTITY);
        engine.messageDispatcher.dispatchMessage(this, Messages.ADD_STAGE_T0_ENTITY);
        engine.messageDispatcher.dispatchMessage(this, Messages.ADD_ENTITY_TO_SCREEN);
        this.setBatch(engine.batch);
        create();
        start();
    }

    //These methods should be implemented in the subclass
    protected abstract HashMap<String, Class> requestAssets();

    protected abstract void assetsResponse(HashMap<String, Object> response);

    protected abstract void handleInput(InputMessage message);

    protected abstract void handleCollision(BaseEntity entity);

    protected abstract void handleInteraction(ArrayList<BaseEntity> list);

    protected abstract boolean handleCustomMessages(Telegram telegram);

    protected abstract void create();

    protected abstract void start();

    protected abstract void process();

    protected abstract void destroy();

    public boolean isIndependentFacing() {
        return independentFacing;
    }

    public void setIndependentFacing(boolean independentFacing) {
        this.independentFacing = independentFacing;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public Stage getUiStage(){
        return this.uiStage;
    }

    public Stage getMainStage(){
        return this.mainStage;
    }

    public void setUiStage(Stage uiStage) {
        this.uiStage = uiStage;
    }

    public void setMainStage(Stage mainStage){
        this.mainStage = mainStage;
    }

    public boolean hasBody(){
        return Mapper.b2BodyComponentMapper.has(this);
    }

    public Body getBody(){
        return body;
    }

    public void setBody(Body body){
        this.body = Tools.addBody(engine.engine, this, body);
    }

    public void setFilter(short categoryBits){
        Tools.addFilter(engine.engine, this, categoryBits);
    }

    public void setFilter(short categoryBits, short maskBits){
        Tools.addFilter(engine.engine, this, categoryBits, maskBits);
    }

    public void setFilter(short categoryBits, short maskBits, short groupIndex){
        Tools.addFilter(engine.engine, this, categoryBits, maskBits, groupIndex);
    }

    public void setFilter(Filter filter){
        Tools.addFilter(engine.engine, this, filter);
    }

    public boolean hasID(){
        return Mapper.idComponentMapper.has(this);
    }

    public int getId(){
        return Mapper.idComponentMapper.get(this).id;
    }

    public void setId(String nameID){
        if(!Mapper.idComponentMapper.has(this)) {
            engine.messageDispatcher.addListener(this, Tools.addID(engine.engine, this, nameID));
            return;
        }
        if(Tools.stringToID(nameID) == Mapper.idComponentMapper.get(this).id) return;
        engine.messageDispatcher.removeListener(this, Mapper.idComponentMapper.get(this).id);
        engine.messageDispatcher.addListener(this, Tools.addID(engine.engine, this, nameID));

    }

    public void setInteraction(short single){
        Tools.addInteraction(engine.engine, this, single);
    }

    public void setInteraction(short first, short second){
        Tools.addInteraction(engine.engine, this, first, second);
    }

    public boolean hasInteractionList(){
        return Mapper.interactionComponentMapper.has(this);
    }

    public ArrayList<CollisionInteraction> getInteractionList(){
        return Mapper.interactionComponentMapper.get(this).collisionInteractionList;
    }

    public void setAnimation(TextureBundleAnimation animation){
        Tools.addAnimation(engine.engine, this, animation);
    }

    public CustomAnimation getAnimation(){
        return Tools.getAnimation(this);
    }

    public void setAnimation(ShapeTextureBundleAnimation animation){
        Tools.addAnimation(engine.engine, this, animation);
    }

    public void setImageOrder(float imageOrder){
        Tools.addImageOrder(engine.engine, this, imageOrder);
    }

    public void addSound(String soundName, Sound sound){
        Tools.addSound(engine.engine, this, engine.messageDispatcher, sound, soundName);
    }

    public void addMusic(String musicName, Music music){
        Tools.addMusic(engine.engine, this, engine.messageDispatcher, music, musicName);
    }

    public SoundBundle getSound(String soundName){
        return Tools.getSound(this, soundName);
    }

    public MusicBundle getMusic(String musicName){
        return Tools.getMusic(this, musicName);
    }

    public void removeSound(String soundName){
        Tools.removeSound(this, soundName);
    }

    public void removeMusic(String musicName){
        Tools.removeMusic(this, musicName);
    }

    public Box2DSprite getSprite(){
        return Tools.getSprite(this);
    };

    public void setBoundingRadius(float boundingRadius){
        this.boundingRadius = boundingRadius;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(msg.message == Messages.INPUT) {
            handleInput((InputMessage) msg.extraInfo);
            return true;
        }
        else if(Mapper.idComponentMapper.has(this) && msg.message == Mapper.idComponentMapper.get(this).id && (msg.extraInfo instanceof InteractionMessage)){
            handleInteraction((InteractionMessage)(msg.extraInfo));
            CollisionPool.freeInteractionMessage((InteractionMessage) msg.extraInfo);
            return true;
        }
        else if(msg.message == Messages.RECEIVE_ASSET){
            if(!(((AssetResponseMessage) msg.extraInfo).assetResponseMap).isEmpty()){
                assetsResponse(((AssetResponseMessage) msg.extraInfo).assetResponseMap);
                AssetResponsePool.freeAssetResponseMessage((AssetResponseMessage) msg.extraInfo);
                return true;
            };
            AssetResponsePool.freeAssetResponseMessage((AssetResponseMessage) msg.extraInfo);
        }
        else if(msg.message == Messages.ADD_STAGE_T0_ENTITY && msg.sender instanceof ScreenSystem){
            StageMessage stageMessage = (StageMessage) msg.extraInfo;
            this.setUiStage(stageMessage.uiStage);
            this.setMainStage(stageMessage.mainStage);
            StagePool.freeStageMessage(stageMessage);
            return true;
        }
        return handleCustomMessages(msg);
    }

    @Override
    public Vector2 getLinearVelocity() {
        return Mapper.b2BodyComponentMapper.get(this).body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return Mapper.b2BodyComponentMapper.get(this).body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return isTagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        zeroThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return Mapper.b2BodyComponentMapper.get(this).body.getPosition();
    }

    @Override
    public float getOrientation() {
        return Mapper.b2BodyComponentMapper.get(this).body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        Mapper.b2BodyComponentMapper.get(this).body.setTransform(getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return Tools.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return Tools.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }
}
