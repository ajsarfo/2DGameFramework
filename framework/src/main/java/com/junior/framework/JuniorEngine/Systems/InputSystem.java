package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.InputMessage;
import com.junior.framework.JuniorEngine.Pools.InputPool;


public class InputSystem extends EntitySystem implements InputProcessor {
    private Camera camera;
    private MessageDispatcher messageDispatcher;
    private static Vector3 vector3 = new Vector3(0, 0 , 0);

    public InputSystem(GameEngine game, int priority){
        super(priority);
        this.camera = game.camera;
        this.messageDispatcher = game.messageDispatcher;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        Gdx.input.setInputProcessor(this);
        InputPool.newInstance(1, 2);
    }

    @Override
    public void removedFromEngine(Engine engine){
        super.removedFromEngine(engine);
        InputPool.flushPool();
    }

    @Override
    public boolean keyDown(int keycode) {
        InputMessage data = InputPool.obtainInputMessage();
        data.keyDown = true;
        data.keycode = keycode;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        InputMessage data = InputPool.obtainInputMessage();
        data.keyUp = true;
        data.keycode = keycode;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        InputMessage data = InputPool.obtainInputMessage();
        data.keyTyped = true;
        data.character = character;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        InputMessage data  = InputPool.obtainInputMessage();
        data.touchDown = true;
        camera.unproject(vector3);
        data.screenX = (int) vector3.x;
        data.screenY = (int) vector3.y;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        InputMessage data = InputPool.obtainInputMessage();
        camera.unproject(vector3);
        data.touchUp = true;
        data.screenX = (int) vector3.x;
        data.screenY = (int) vector3.y;
        data.pointer = pointer;
        data.button = button;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        InputMessage data = InputPool.obtainInputMessage();
        camera.unproject(vector3);
        data.touchDragged = true;
        data.screenX = (int) vector3.x;
        data.screenY = (int) vector3.y;
        data.pointer = pointer;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        InputMessage data = InputPool.obtainInputMessage();
        camera.unproject(vector3);
        data.mouseMoved = true;
        data.screenX = (int) vector3.x;
        data.screenY = (int) vector3.y;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        InputMessage data = InputPool.obtainInputMessage();
        data.mouseScrolled = true;
        data.amount = amount;
        messageDispatcher.dispatchMessage(Messages.INPUT, data);
        InputPool.freeInputMessage(data);
        return false;
    }
}
