package com.junior.framework.JuniorEngine.Internal;


import com.junior.framework.JuniorEngine.Utils.Tools;

public final class Messages {
    public static final int INPUT = Tools.stringToID("input_message");
    public static final int ADD_ENTITY_TO_SCREEN = Tools.stringToID("add_entity_to_screen_message");
    public static final int SEND_STAGE_TO_RENDER_SYSTEM = Tools.stringToID("send_stage_to_render_system_message");
    public static final int ADD_STAGE_T0_ENTITY = Tools.stringToID("add_stage_to_entity_message");
    public static final int RETRIEVE_ASSET = Tools.stringToID("retrieve_asset_message");
    public static final int RECEIVE_ASSET = Tools.stringToID(("receive_asset_message"));
    public static final int SOUND = Tools.stringToID("sound");
    public static final int MUSIC = Tools.stringToID("music");
}