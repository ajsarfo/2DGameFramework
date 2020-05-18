package com.junior.framework.JuniorEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.junior.framework.JuniorEngine.Extensions.ShapeTextureBundleAnimation;
import com.junior.framework.JuniorEngine.Extensions.TextureBundleAnimation;


public class AnimationComponent implements Component, Pool.Poolable {
    public boolean isTextureAnimation = true;
    public ShapeTextureBundleAnimation shapeTextureAnimation;
    public TextureBundleAnimation textureAnimation;

    @Override
    public void reset() {
        isTextureAnimation = true;
        shapeTextureAnimation = null;
        textureAnimation = null;
    }
}
