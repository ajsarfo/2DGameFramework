package com.junior.framework.JuniorEngine.Extensions;

public class CustomAnimation<T> {
    private T[] frames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;
    private boolean loop, isPaused;

    public CustomAnimation(T[] frames, float duration, boolean loop){
        this.frames = frames;
        this.frameCount = frames.length;
        this.maxFrameTime = duration / (float) frameCount;
        this.frame = 0;
        this.currentFrameTime = 0;
        this.loop = loop;
        this.isPaused = false;
    }

    public void update(float dt){
        if(isPaused) return;

        currentFrameTime += dt;
        if(currentFrameTime > maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }

        if(!(frame >= frameCount)) return;

        if(loop) frame = 0;
        else frame-= frameCount;
    }

    public void setLoop(boolean loop){
        this.loop = loop;
    }

    public boolean isLoop(){
        return loop;
    }

    public void pause(){
        isPaused = true;
    }

    public boolean isPlaying(){
        return !isPaused;
    }

    public void play(){
        isPaused = false;
    }

    public void reset(){
        currentFrameTime = 0;
    }

    public T getCurrentFrame(){
        return frames[frame];
    }

    public T[] getFrames(){
        return frames;
    }
}

