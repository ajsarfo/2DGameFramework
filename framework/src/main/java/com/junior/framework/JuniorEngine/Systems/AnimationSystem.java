package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.junior.framework.JuniorEngine.Components.AnimationComponent;
import com.junior.framework.JuniorEngine.Components.B2BodyComponent;
import com.junior.framework.JuniorEngine.Extensions.CustomB2DSprite;
import com.junior.framework.JuniorEngine.Extensions.ShapeTextureBundleAnimation;
import com.junior.framework.JuniorEngine.Extensions.TextureBundleAnimation;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Utils.Mapper;
import com.junior.framework.JuniorEngine.Utils.Tools;

public class AnimationSystem extends IntervalIteratingSystem {

    private static Family family = Family.all(AnimationComponent.class, B2BodyComponent.class).get();

    public AnimationSystem(int priority) {
        super(family, GameEngine.FPS, priority);
    }

    @Override
    protected void processEntity(Entity entity) {
        AnimationComponent animationComponent = Mapper.animationComponentMapper.get(entity);

        if(!animationComponent.isTextureAnimation){
            ShapeTextureBundleAnimation bundleAnimation = animationComponent.shapeTextureAnimation;
            Body body = Mapper.b2BodyComponentMapper.get(entity).body;

            if(bundleAnimation == null) return;

            if(!(body.getUserData() instanceof CustomB2DSprite)) return;
            CustomB2DSprite sprite = (CustomB2DSprite) body.getUserData();

            if(sprite == null || sprite.getIndex() == bundleAnimation.getCurrentBundle().getIndex()){
                return;
            }

            Tools.attachShapeAndSpriteToEntity(entity, bundleAnimation.getCurrentBundle());
            return;
        }

        TextureBundleAnimation bundleAnimation = animationComponent.textureAnimation;
        Body body = Mapper.b2BodyComponentMapper.get(entity).body;

        if(bundleAnimation == null) return;

        if(!(body.getUserData() instanceof CustomB2DSprite)) return;
        CustomB2DSprite sprite = (CustomB2DSprite) body.getUserData();

        if(sprite == null || sprite.getIndex() == bundleAnimation.getCurrentBundle().getIndex()) return;

        Tools.attachSpriteToEntity(entity, bundleAnimation.getCurrentBundle());
    }

    @Override
    protected void updateInterval() {
        super.updateInterval();
        for(Entity entity: this.getEngine().getEntitiesFor(family)){
            AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
            if(!animationComponent.isTextureAnimation){
                ShapeTextureBundleAnimation shapeTextureBundleAnimation = animationComponent.shapeTextureAnimation;
                shapeTextureBundleAnimation.update(GameEngine.FPS);
                continue;
            }

            TextureBundleAnimation textureBundleAnimation = animationComponent.textureAnimation;
            textureBundleAnimation.update(GameEngine.FPS);
        }
    }
}
