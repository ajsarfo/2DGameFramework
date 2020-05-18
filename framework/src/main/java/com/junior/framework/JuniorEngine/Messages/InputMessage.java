package com.junior.framework.JuniorEngine.Messages;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Pool;

public class InputMessage implements Pool.Poolable {
    public  boolean keyDown, keyUp, keyTyped, touchDown, touchUp, touchDragged, mouseMoved, mouseScrolled;
    public int keycode = Input.Keys.ENTER;
    public int amount, screenX, screenY, pointer, button = 0;
    public char character = 0;

    @Override
    public void reset() {
        keyDown = keyUp = touchUp = touchDown = keyTyped =  touchDragged = mouseMoved = mouseScrolled = false;
        keycode = Input.Keys.ENTER;
        amount = screenX = screenY = pointer = button = character = 0;
    }
}
