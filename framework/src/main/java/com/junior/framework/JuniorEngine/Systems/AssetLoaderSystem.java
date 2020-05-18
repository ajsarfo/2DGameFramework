package com.junior.framework.JuniorEngine.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.junior.framework.JuniorEngine.Internal.AssetLoader;
import com.junior.framework.JuniorEngine.Internal.GameEngine;
import com.junior.framework.JuniorEngine.Internal.Messages;
import com.junior.framework.JuniorEngine.Messages.AssetRequestMessage;
import com.junior.framework.JuniorEngine.Messages.AssetResponseMessage;
import com.junior.framework.JuniorEngine.Pools.AssetRequestPool;
import com.junior.framework.JuniorEngine.Pools.AssetResponsePool;

import java.util.Map;

public class AssetLoaderSystem extends EntitySystem implements Telegraph {
    private MessageDispatcher messageDispatcher;
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public AssetLoaderSystem(GameEngine engine, int priority){
        super(priority);
        messageDispatcher = engine.messageDispatcher;
    }

    private void assetLoaders(){
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    private boolean loadNonFontAssets(Map<String, Class> assetMap){
        for(Map.Entry<String, Class> assetEntry : assetMap.entrySet())
            assetManager.load(assetEntry.getKey(), (Class) assetEntry.getValue());
        return true;
    }

    private boolean loadFontAssets(Map<String, AssetLoader.FontAsset> assetParamFont){
        if(assetParamFont == null) return false;
        for(Map.Entry<String, AssetLoader.FontAsset> entry : assetParamFont.entrySet()){
            FreetypeFontLoader.FreeTypeFontLoaderParameter loaderParam = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            loaderParam.fontFileName = entry.getValue().fileLocation;
            loaderParam.fontParameters = entry.getValue().fontParameter;
            assetManager.load(entry.getKey(), BitmapFont.class, loaderParam);
        }
        return true;
    }

    private void finishLoadingAndApplyFilters(){
        assetManager.finishLoading();

        //Retrieve texture atlas if any;
        for(TextureAtlas atlas : assetManager.getAll(TextureAtlas.class, new Array<TextureAtlas>())){
            textureAtlas = atlas;
            break;
        }

        //Apply linear_linear filter to textures in assetManager
        Array<Texture> filterTextureArray = new Array<>();
        assetManager.getAll(Texture.class, filterTextureArray);
        for(Texture texture : filterTextureArray) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void loadAssets(AssetLoader assetLoader){
        if(assetLoader == null) return;
        boolean finish = false;
        finish = loadFontAssets(assetLoader.fontParameterMap) | loadNonFontAssets(assetLoader.assetMap);
        if(finish) finishLoadingAndApplyFilters();
    }

    @Override
    public void addedToEngine(Engine engine){
        super.addedToEngine(engine);
        assetManager = new AssetManager();
        assetLoaders();
        AssetRequestPool.newInstance(2, 100);
        AssetResponsePool.newInstance(2, 100);
        messageDispatcher.addListener(this, Messages.RETRIEVE_ASSET);
    }

    @Override
    public void removedFromEngine(Engine engine){
        super.removedFromEngine(engine);
        messageDispatcher.removeListener(this, Messages.RETRIEVE_ASSET);
        assetManager.dispose();
        AssetRequestPool.flushPool();
        AssetResponsePool.flushPool();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(!(msg.message == Messages.RETRIEVE_ASSET)) return false;

        AssetRequestMessage assetRequestMessage = (AssetRequestMessage) msg.extraInfo;
        if(assetRequestMessage.assetMap.isEmpty()) return true;

        AssetResponseMessage assetResponseMessage = AssetResponsePool.obtainAssetResponseMessage();

        for(Map.Entry<String, Class> requestEntry : assetRequestMessage.assetMap.entrySet()){
            Object asset;
            try{
                asset = assetManager.get(requestEntry.getKey(), requestEntry.getValue());

                if(asset instanceof Texture) asset = new TextureRegion((Texture) asset);

                assetResponseMessage.assetResponseMap.put(requestEntry.getKey(), asset);
            }
            catch(Exception e){
                if(requestEntry.getValue() == Texture.class && !(textureAtlas == null)){
                    assetResponseMessage.assetResponseMap.put(requestEntry.getKey(), textureAtlas.findRegion(requestEntry.getKey()));
                    continue;
                }
                throw(e);
            }
        }

        AssetRequestPool.freeAssetRequestMessage(assetRequestMessage);
        messageDispatcher.dispatchMessage(this, msg.sender, Messages.RECEIVE_ASSET, assetResponseMessage);
        return true;
    }
}
