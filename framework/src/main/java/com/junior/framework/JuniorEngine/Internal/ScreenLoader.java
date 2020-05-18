package com.junior.framework.JuniorEngine.Internal;



import com.junior.framework.JuniorEngine.Bases.BaseScreen;

import java.util.HashMap;

public class ScreenLoader {

    private Class startScreen, globalScreen;
    private HashMap<Class, BaseScreen> screens;

    public ScreenLoader(){
        new ScreenLoader(3);
    }

    public ScreenLoader(int screenSize){
        this.screens = new HashMap<>(screenSize);
    }

    public ScreenLoader addScreen(BaseScreen screen){
        screens.put(screen.getClass(), screen);
        return this;
    }

    public ScreenLoader addScreens(BaseScreen ... screens){
        for(int i = 0 ; i < screens.length; i++)
            this.screens.put(screens[i].getClass(), screens[i]);
        return this;
    }

    public ScreenLoader setStartScreen(Class startScreen){
        this.startScreen = startScreen;
        return this;
    }

    public ScreenLoader setGlobalScreen(Class globalScreen) {
        this.globalScreen = globalScreen;
        return this;
    }

    public HashMap<Class, BaseScreen> getScreens(){
        return this.screens;
    }

    public Class getStartScreen() {
        return this.startScreen;
    }

    public BaseScreen getGlobalScreen() {
        return screens.get(globalScreen);
    }
}
