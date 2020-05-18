package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ShapeTextureBundle {

    private int index;
    private Array<Vector2[]> shape;

    private TextureRegion sprite;

    public ShapeTextureBundle(Array<Vector2[]> shape, TextureRegion sprite, int index) {
        this.index = index;
        this.shape = shape;
        this.sprite = sprite;
    }

    public int getIndex() {
        return index;
    }

    public Array<Vector2[]> getShape() {
        return shape;
    }

    public TextureRegion getSprite() {
        return sprite;
    }
}

