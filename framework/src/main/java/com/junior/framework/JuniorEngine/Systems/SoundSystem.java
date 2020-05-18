package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.junior.framework.JuniorEngine.Extensions.SoundBundle;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.SoundMessage;
import com.junior.framework.JuniorEngine.Pools.SoundPool;
import com.junior.framework.JuniorEngine.Utils.Mapper;


public class SoundSystem extends EntitySystem implements Telegraph {
    MessageDispatcher messageDispatcher;

    public SoundSystem(GameEngine game){
       this.messageDispatcher = game.messageDispatcher;
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        messageDispatcher.addListener(this, Messages.SOUND);
        SoundPool.newInstance(2, 10);
    }

    @Override
    public void removedFromEngine(Engine engine){
        super.removedFromEngine(engine);
        messageDispatcher.removeListener(this);
        SoundPool.flushPool();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(msg.message != Messages.SOUND || !(msg.extraInfo instanceof SoundMessage)) return true;
        SoundMessage soundMessage = (SoundMessage) msg.extraInfo;

        if(soundMessage.entity == null || !Mapper.soundComponentMapper.has(soundMessage.entity)){
            SoundPool.freeSoundMessage(soundMessage);
            return true;
        }

        if(Mapper.soundComponentMapper.get(soundMessage.entity).soundMap == null) {
            SoundPool.freeSoundMessage(soundMessage);
            return true;
        }

        if(Mapper.soundComponentMapper.get(soundMessage.entity).soundMap.isEmpty()){
            SoundPool.freeSoundMessage(soundMessage);
            return true;
        }

        SoundBundle soundBundle = Mapper.soundComponentMapper.get(soundMessage.entity).soundMap.get(soundMessage.soundName);

        if(soundBundle == null){
            SoundPool.freeSoundMessage(soundMessage);
            return true;
        }

        processSound(soundBundle);

        SoundPool.freeSoundMessage(soundMessage);
        return true;
    }

    private void processSound(SoundBundle bundle) {

              if(bundle.getID() != -1){
                  if(bundle.isStopped()) {
                      bundle.getSound().stop(bundle.getID());
                      bundle.clearID();
                  };

                  if(bundle.isPaused()) bundle.getSound().pause(bundle.getID());

                  if(bundle.isResumed()) bundle.getSound().resume(bundle.getID());
              }

              if(!bundle.isPlayed()) return;

              long id = bundle.getSound().play();
              bundle.getSound().setVolume(id, bundle.getVolume());
              bundle.getSound().setPitch(id, bundle.getPitch());
              bundle.getSound().setPan(id, bundle.getPan(), bundle.getVolume());
              bundle.setID(id);

              if(bundle.isLooped()) bundle.getSound().loop(id);

              bundle.finishedPausing();
              bundle.finishedResuming();
              bundle.finishedStopping();
              bundle.finishedLooping();
    }
}
