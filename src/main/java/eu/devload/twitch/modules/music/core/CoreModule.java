package eu.devload.twitch.modules.music.core;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.music.core.event.TwitchGoLive;
import eu.devload.twitch.modules.music.core.event.TwitchGoOffline;

public class CoreModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerEvent(new TwitchGoLive());
        registerEvent(new TwitchGoOffline());

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return null;
    }
}
