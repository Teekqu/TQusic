package eu.devload.twitch.modules.music.commands;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class MusicCommandModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "musiccommands";
    }

    @Override
    public String name() {
        return "Music Commands Module";
    }

    @Override
    public String description() {
        return "Controls the spotify music via commands.";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public boolean canDisable() {
        return false;
    }

    @Override
    public List<String> authors() {
        return List.of("Teekqu");
    }
}
