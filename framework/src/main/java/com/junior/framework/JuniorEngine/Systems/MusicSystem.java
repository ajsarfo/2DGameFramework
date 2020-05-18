package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.junior.framework.JuniorEngine.Extensions.MusicBundle;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.MusicMessage;
import com.junior.framework.JuniorEngine.Pools.MusicPool;
import com.junior.framework.JuniorEngine.Utils.Mapper;

public class MusicSystem extends EntitySystem implements Telegraph {
    MessageDispatcher messageDispatcher;

    public MusicSystem(GameEngine game){
        messageDispatcher = game.messageDispatcher;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        messageDispatcher.addListener(this, Messages.MUSIC);
        MusicPool.newInstance(2, 10);
    }

    @Override
    public void removedFromEngine(Engine engine){
        super.removedFromEngine(engine);
        messageDispatcher.removeListener(this);
        MusicPool.flushPool();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(msg.message != Messages.MUSIC || !(msg.extraInfo instanceof MusicMessage)) return true;
        MusicMessage musicMessage = (MusicMessage) msg.extraInfo;

        if(musicMessage.entity == null || !Mapper.musicComponentMapper.has(musicMessage.entity)){
            MusicPool.freeMusicMessage(musicMessage);
            return true;
        }

        if(Mapper.musicComponentMapper.get(musicMessage.entity).musicMap == null) {
            MusicPool.freeMusicMessage(musicMessage);
            return true;
        }

        if(Mapper.musicComponentMapper.get(musicMessage.entity).musicMap.isEmpty()){
            MusicPool.freeMusicMessage(musicMessage);
            return true;
        }

        MusicBundle musicBundle = Mapper.musicComponentMapper.get(musicMessage.entity).musicMap.get(musicMessage.musicName);

        if(musicBundle == null){
            MusicPool.freeMusicMessage(musicMessage);
            return true;
        }

        processMusic(musicBundle);

        MusicPool.freeMusicMessage(musicMessage);
        return true;
    }

    public void processMusic(MusicBundle musicBundle){
        if(musicBundle.queryPositionSet()){
            musicBundle.getMusic().setPosition(musicBundle.queryPosition());
            musicBundle.clearPositionSet();
        }

        if(musicBundle.queryPanSet()){
            musicBundle.getMusic().setPan(musicBundle.queryPan(), musicBundle.queryPanVolume());
            musicBundle.clearPanSet();
        }
        if(musicBundle.queryVolumeSet()){
            musicBundle.getMusic().setVolume(musicBundle.queryVolume());
            musicBundle.clearVolumeSet();
        }

       if(musicBundle.queryPaused()){
           musicBundle.getMusic().pause();
           musicBundle.finishedPausing();
       }

       if(musicBundle.queryStopped()){
           musicBundle.getMusic().stop();
           if(!musicBundle.queryPlayed()) return;
           musicBundle.finishedStopping();
       }

       if(musicBundle.queryPlayed()){
           musicBundle.getMusic().setVolume(musicBundle.getVolume());
           musicBundle.getMusic().play();
           musicBundle.finishedPlaying();
        }

        if(musicBundle.queryLooped()){
            musicBundle.getMusic().setVolume(musicBundle.getVolume());
            musicBundle.getMusic().setLooping(true);
            musicBundle.finishedLooping();
        }
    }
}
