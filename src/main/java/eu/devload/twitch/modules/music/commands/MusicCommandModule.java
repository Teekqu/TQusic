package eu.devload.twitch.modules.music.commands;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.music.commands.commands.*;

public class MusicCommandModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerCommand(new Pause());
        registerCommand(new Previous());
        registerCommand(new Resume());
        registerCommand(new Skip());
        registerCommand(new Song());
        registerCommand(new Volume());
        registerCommand(new add_to_queue());

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new MusicCommandModuleInfo();
    }
}
