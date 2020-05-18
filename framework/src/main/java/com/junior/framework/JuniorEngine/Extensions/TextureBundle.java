package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureBundle {
    private TextureRegion sprite;
    private int index;

    public TextureBundle(TextureRegion sprite, int index){
        this.sprite = sprite;
        this.index = index;
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    public int getIndex() {
        return index;
    }

    public void scale(float scale) {
        sprite.setRegionWidth((int) (sprite.getRegionWidth() * scale));
        sprite.setRegionHeight((int) (sprite.getRegionHeight() * scale));
    }
}

