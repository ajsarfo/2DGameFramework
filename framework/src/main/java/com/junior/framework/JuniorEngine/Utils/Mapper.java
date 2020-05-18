package com.junior.framework.JuniorEngine.Utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.junior.framework.JuniorEngine.Components.AnimationComponent;
import com.junior.framework.JuniorEngine.Components.B2BodyComponent;
import com.junior.framework.JuniorEngine.Components.BehaviourComponent;
import com.junior.framework.JuniorEngine.Components.FilterComponent;
import com.junior.framework.JuniorEngine.Components.IDComponent;
import com.junior.framework.JuniorEngine.Components.ImageOrderComponent;
import com.junior.framework.JuniorEngine.Components.InteractionComponent;
import com.junior.framework.JuniorEngine.Components.MusicComponent;
import com.junior.framework.JuniorEngine.Components.SoundComponent;


public abstract class Mapper {
    public static ComponentMapper<InteractionComponent> interactionComponentMapper = ComponentMapper.getFor(InteractionComponent.class);
    public static ComponentMapper<IDComponent> idComponentMapper = ComponentMapper.getFor(IDComponent.class);
    public static ComponentMapper<B2BodyComponent> b2BodyComponentMapper = ComponentMapper.getFor(B2BodyComponent.class);
    public static ComponentMapper<BehaviourComponent> behaviourComponentMapper = ComponentMapper.getFor(BehaviourComponent.class);
    public static ComponentMapper<AnimationComponent> animationComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
    public static ComponentMapper<FilterComponent> filterComponentMapper = ComponentMapper.getFor(FilterComponent.class);
    public static ComponentMapper<ImageOrderComponent> imageOrderComponentMapper = ComponentMapper.getFor(ImageOrderComponent.class);
    public static ComponentMapper<SoundComponent> soundComponentMapper = ComponentMapper.getFor(SoundComponent.class);
    public static ComponentMapper<MusicComponent> musicComponentMapper = ComponentMapper.getFor(MusicComponent.class);
}
