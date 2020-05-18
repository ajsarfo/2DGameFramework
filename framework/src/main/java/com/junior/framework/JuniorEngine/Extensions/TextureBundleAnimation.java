package com.junior.framework.JuniorEngine.Extensions;

public class TextureBundleAnimation extends CustomAnimation<TextureBundle> {
    float scale;

    public TextureBundleAnimation(TextureBundle[] frames, float duration, boolean loop) {
        super(frames, duration, loop);
        this.scale = 0f;
    }

    public void flip(boolean x, boolean y){
        for(TextureBundle shapeTextureBundle : getFrames())
            shapeTextureBundle.getSprite().flip(x, y);
    }

    public TextureBundle getCurrentBundle(){
        return getCurrentFrame();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
