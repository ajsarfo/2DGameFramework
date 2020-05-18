package com.junior.framework.JuniorEngine.Internal;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.HashSet;

public class AssetLoader {
    public HashMap<String, Class> assetMap;
    public HashMap<String, FontAsset> fontParameterMap;
    public HashSet<String> fileLocations;

    public void addAsset(Class object, String ... assetName){
        if(this.assetMap == null) this.assetMap = new HashMap<>();
        for(int i = 0; i < assetName.length ; i++) assetMap.put(assetName[i], object);
    }

    public void addAsset(String ttf_file_location, String name, FreeTypeFontGenerator.FreeTypeFontParameter param){
        if(fontParameterMap == null) fontParameterMap = new HashMap<>(2);
        if(fileLocations == null) fileLocations = new HashSet<>(2);
        fileLocations.add(ttf_file_location);
        fontParameterMap.put(name, new FontAsset(ttf_file_location, param));
    }

    public class FontAsset {
        public String fileLocation;
        public FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;

        public FontAsset(String fileLocation, FreeTypeFontGenerator.FreeTypeFontParameter fontParameter){
            this.fileLocation = fileLocation;
            this.fontParameter = fontParameter;
        }
    }
}
