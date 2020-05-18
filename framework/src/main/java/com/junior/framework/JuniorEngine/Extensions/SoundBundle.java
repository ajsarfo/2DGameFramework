package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.audio.Sound;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.SoundMessage;
import com.junior.framework.JuniorEngine.Pools.SoundPool;


public class SoundBundle {
    private Sound sound;
    private String soundName;
    private boolean play, pause, resume, stop, usePitch, usePan, loop;
    private float volume, pitch, pan;
    private long id;

    private MessageDispatcher messageDispatcher;
    private Entity owner;

    public SoundBundle(Sound sound, String soundName, MessageDispatcher messageDispatcher, Entity owner){
        this.messageDispatcher = messageDispatcher;
        this.owner = owner;
        this.sound = sound;
        this.soundName = soundName;
        this.id = -1;
        reset();
    }

    public Sound getSound(){
        return this.sound;
    }

    public void play(){
        this.play = true;
        finish();
    }

    public void pause(){
        this.pause = true;
        this.resume = false;
        finish();
    }

    public void stop(){
        this.stop = true;
        finish();
    }

    public void resume(){
        this.resume = true;
        pause = false;
        finish();
    }

    public void loop(){
        this.loop = true;
        this.play = true;
        finish();
    }

    public boolean isResumed(){
        return resume;
    }

    public boolean isPaused(){
        return pause;
    }

    public boolean isPlayed(){
        return play;
    }

    public boolean isStopped(){
        return stop;
    }

    public boolean isLooped(){
        return loop;
    }

    public SoundBundle setVolume(float volume){
        this.volume = volume;
        return this;
    }

    public SoundBundle setPitch(float pitch){
        this.pitch = pitch;
        return this;
    }

    public SoundBundle setPan(float pan){
        this.pan = pan;
        return this;
    }

    //*************** For Sound System Only *************
    public void finishedPlaying(){
        play = false;
    }

    public void finishedPausing(){
        pause = false;
    }

    public void finishedLooping(){
        play = false;
        loop = false;
    }

    public void finishedResuming(){
        resume = false;
    }

    public void finishedStopping(){
        stop = false;
    }

    public void setID(long id){
        this.id = id;
    }

    public long getID(){
        return id;
    }

    public void clearID(){
        this.id = -1;
    }

    //***************************************************

    public float getPitch(){
        return pitch;
    }

    public float getPan(){
        return pan;
    }

    public float getVolume(){
        return volume;
    }

    public void reset(){
        id = -1;
        pitch = 1;
        pan = 0.5f;
        volume = 1f;
        loop = play = pause = resume = false;
        finish();
    }

    private void finish(){
        SoundMessage soundMessage = SoundPool.obtainSoundMessage();
        soundMessage.entity = owner;
        soundMessage.soundName = soundName;
        messageDispatcher.dispatchMessage(Messages.SOUND, soundMessage);
    }
}
