package eu.devload.twitch.modules.music.points;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class MusicPointsModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "musicpoints";
    }

    @Override
    public String name() {
        return "Music Points Module";
    }

    @Override
    public String description() {
        return "Controls the spotify music via channel points.";
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
