package eu.devload.twitch.interfaces;

import java.util.List;

public interface ModuleInfo {

    String identifier();
    String name();
    String description();
    String version();
    boolean canDisable();
    List<String> authors();

}
