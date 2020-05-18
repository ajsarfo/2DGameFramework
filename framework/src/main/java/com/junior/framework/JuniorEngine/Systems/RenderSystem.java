package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.StageMessage;
import com.junior.framework.JuniorEngine.Pools.StagePool;


import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class RenderSystem extends EntitySystem implements Telegraph {
    World world;
    Batch batch;
    OrthographicCamera camera;
    Stage currentScreenMainStage, currentScreenUiStage;
    MessageDispatcher messageDispatcher;
    GameEngine game;

    public RenderSystem(GameEngine game, int priority) {
        super(priority);
        world = game.world;
        batch = game.batch;
        camera = game.camera;
        messageDispatcher = game.messageDispatcher;
        this.game = game;
    }

    @Override
    public void update(float dt) {
        messageDispatcher.dispatchMessage(this, Messages.SEND_STAGE_TO_RENDER_SYSTEM);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Box2DSprite.draw(batch, world, true);
        batch.end();
        if(currentScreenMainStage != null) {
            batch.setProjectionMatrix(currentScreenMainStage.getViewport().getCamera().combined);
            currentScreenMainStage.draw();
        }
        if(currentScreenUiStage != null) {
            batch.setProjectionMatrix(currentScreenUiStage.getViewport().getCamera().combined);
            currentScreenUiStage.draw();
        }
        currentScreenMainStage = currentScreenUiStage = null;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        messageDispatcher.addListener(this, Messages.SEND_STAGE_TO_RENDER_SYSTEM);
        StagePool.newInstance(1, 10);
        Box2DSprite.setZComparator((box2DSprite1, box2DSprite2) -> {
            if(box2DSprite1.getZIndex() < box2DSprite2.getZIndex()) return -1;
            else if(box2DSprite1.getZIndex() > box2DSprite2.getZIndex()) return 1;
            return 0;
        });
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(msg.message != Messages.SEND_STAGE_TO_RENDER_SYSTEM || !(msg.sender instanceof ScreenSystem)) return false;
        currentScreenMainStage = ((StageMessage) msg.extraInfo).mainStage;
        currentScreenUiStage = ((StageMessage) msg.extraInfo).uiStage;
        StagePool.freeStageMessage(((StageMessage) msg.extraInfo));
        return true;
    }
}
