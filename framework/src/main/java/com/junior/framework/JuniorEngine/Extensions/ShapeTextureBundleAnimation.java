package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.gdx.math.Vector2;

public class ShapeTextureBundleAnimation extends CustomAnimation<ShapeTextureBundle> {
    float scale;

    public ShapeTextureBundleAnimation(ShapeTextureBundle[] frames, float duration, boolean loop) {
        super(frames, duration, loop);
    }

    public void flip(boolean x, boolean y){
        for(ShapeTextureBundle shapeTextureBundle : getFrames()){
            int xDirection =  x ? -1  : 1;
            int yDirection = y ? -1 : 1;

            shapeTextureBundle.getSprite().flip(x, y);

            for(Vector2[] vertices : shapeTextureBundle.getShape())
                for(int i = 0 ; i < vertices.length ; i++)
                    vertices[i].scl(xDirection, yDirection);
        }
    }

    public ShapeTextureBundle getCurrentBundle(){
        return getCurrentFrame();
    }

    public float getScale(){
        return scale;
    }

    public void setScale(float scale){
        this.scale = scale;
    }
}

