package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class CustomB2DSprite extends Box2DSprite {
    private Object userData;
    private int index;

    public CustomB2DSprite(TextureRegion textureRegion){
        super(textureRegion);
        index = -1;
    }

    public CustomB2DSprite(){
        super();
    }

    public CustomB2DSprite(Texture texture){
        super(texture);
    }

    public void setUserData(Object object){
        this.userData = object;
    }

    public Object getUserData(){
        return userData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }
}

