package com.junior.framework.JuniorEngine.Extensions;

public class CollisionInteraction {
    public short singleInteraction;
    public short[] doubleInteraction;
    public boolean isSingle;

    public CollisionInteraction(short single){
        isSingle = true;
        singleInteraction = single;
    }

    public CollisionInteraction(short first, short second){
        isSingle = false;
        doubleInteraction = new short[]{first, second};
    }

    public boolean equals(short first, short second){
        return (first == this.doubleInteraction[0] && second == this.doubleInteraction[0]) || (first == this.doubleInteraction[1] && second == this.doubleInteraction[1]);
    }

    public boolean equals(short single){
        return single == this.singleInteraction;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof CollisionInteraction)) return false;
        CollisionInteraction collisionInteraction = (CollisionInteraction) object;
        if(collisionInteraction.isSingle && this.isSingle) return collisionInteraction.singleInteraction == this.singleInteraction;
        else if((!collisionInteraction.isSingle) && (!this.isSingle))
            return collisionInteraction.doubleInteraction[0] == this.doubleInteraction[0] && collisionInteraction.doubleInteraction[1] == this.doubleInteraction[1];
        return false;
    }
}

