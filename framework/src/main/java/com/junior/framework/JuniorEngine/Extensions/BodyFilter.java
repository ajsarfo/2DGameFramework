package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;

public class BodyFilter {
    private Body body;
    private Filter filter;

    public BodyFilter(Body body){
        this.body = body;
    }

    public BodyFilter(Filter filter){
        this.filter = filter;
    }

    public BodyFilter(Body body, Filter filter){
        this.body = body;
        this.filter = filter;
    }

    public BodyFilter(Body body, short categoryBits){
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        this.body = body;
        this.filter = filter;
    }

    public BodyFilter(Body body, short categoryBits, short maskBits){
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        this.body = body;
        this.filter = filter;
    }

    public BodyFilter(Body body, short categoryBits, short maskBits, short groupIndex){
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;
        this.body = body;
        this.filter = filter;
    }

    public Body getBody() {
        return body;
    }

    public Filter getFilter(){
        return filter;
    }

    public boolean hasFilter(){
        return !(filter == null);
    }

    public boolean hasBody(){
        return !(body == null);
    }
}

