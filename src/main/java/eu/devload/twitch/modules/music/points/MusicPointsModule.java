package eu.devload.twitch.modules.music.points;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.music.points.events.ChannelRewardClaimed;

public class MusicPointsModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerEvent(new ChannelRewardClaimed());

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new MusicPointsModuleInfo();
    }
}
