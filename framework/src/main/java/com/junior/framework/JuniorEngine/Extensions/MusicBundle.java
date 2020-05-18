package com.junior.framework.JuniorEngine.Extensions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.audio.Music;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.MusicMessage;
import com.junior.framework.JuniorEngine.Pools.MusicPool;


public class MusicBundle {
    private  MessageDispatcher messageDispatcher;
    private Music music;
    private Music.OnCompletionListener onCompletionListener;
    private String musicName;

    private boolean play, pause, stop, loop;
    private boolean positionSet, volumeSet, panSet;

    float position, volume, pan, panVolume;

    Entity owner;

    public MusicBundle(Music music, String musicName, MessageDispatcher messageDispatcher, Entity entity){
        this.music = music;
        this.musicName = musicName;
        this.messageDispatcher =  messageDispatcher;
        this.owner = entity;
    }

    public Music getMusic(){
        return music;
    }

    public void play() {
        this.play = true;
        finish();
    }

    public void pause() {
        this.pause = true;
        finish();
    }

    public void stop() {
        this.stop = true;
        finish();
    };

    public boolean isPaused(){
        return !music.isPlaying();
    }

    public boolean isPlaying(){
        return music.isPlaying();
    }

    public boolean isStopped(){
        return isPaused();
    }

    public boolean isLooping(){
        return music.isLooping();
    }


    public float getVolume() {
        return this.music.getVolume();
    }

    public float getPosition(){
        return this.music.getPosition();
    }

    public MusicBundle setVolume(float volume) {
        this.volumeSet = true;
        this.volume = volume;
        return this;
    }

    public MusicBundle setPosition(float position){
        this.positionSet = true;
        this.position = position;
        return this;
    }

    public MusicBundle setPan(float pan, float panVolume){
        panSet = true;
        this.pan = pan;
        this.panVolume = panVolume;
        return this;
    }

    public MusicBundle setOnCompletionListener(Music.OnCompletionListener listener){
        music.setOnCompletionListener(listener);
        this.onCompletionListener = listener;
        return this;
    }

    public Music.OnCompletionListener getOnCompletionListener(){
        return onCompletionListener;
    }

    //************************ For the music system only**********************//
    public boolean queryPlayed(){
        return play;
    }

    public boolean queryPaused() {
        return pause;
    }

    public boolean queryStopped() {
        return stop;
    }

    public boolean queryLooped(){
        return loop;
    }

    public float queryPosition(){
        return position;
    }

    public float queryVolume(){
        return volume;
    }

    public float queryPan(){
         return pan;
    }

    public float queryPanVolume(){
        return panVolume;
    }

    public boolean queryPositionSet(){
        return positionSet;
    }

    public boolean queryVolumeSet(){
        return volumeSet;
    }

    public boolean queryPanSet() {
        return panSet;
    }

    public void finishedPlaying(){
        play = stop = false;
    }

    public void finishedPausing(){
        pause = false;
    }

    public void finishedLooping(){
        loop = false;
    }

    public void finishedStopping(){
        stop = false;
    }

    public void clearPositionSet(){
        positionSet = false;
    }

    public void clearVolumeSet(){
        volumeSet = false;
    }

    public void clearPanSet() {
        panSet = false;
    }
    //**********************************************************************//

    public void reset(){
        pan = 0.5f;
        volume = 1f;
        position = 0;
        loop = play = pause = false;
        finish();
    }

    public void finish(){
        MusicMessage musicMessage = MusicPool.obtainMusicMessage();
        musicMessage.entity = owner;
        musicMessage.musicName = musicName;
        messageDispatcher.dispatchMessage(Messages.MUSIC, musicMessage);
    }
}
