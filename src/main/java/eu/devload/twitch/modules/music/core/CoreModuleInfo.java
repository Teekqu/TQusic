package eu.devload.twitch.modules.music.core;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class CoreModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "core";
    }

    @Override
    public String name() {
        return "Core";
    }

    @Override
    public String description() {
        return "Basic functions for TQusic";
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
