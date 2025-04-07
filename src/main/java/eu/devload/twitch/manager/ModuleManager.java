package eu.devload.twitch.manager;

import eu.devload.twitch.interfaces.TwitchModule;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class ModuleManager {

    private List<TwitchModule> modules = new ArrayList<>();

    public void registerModule(TwitchModule module) {
        if(modules.contains(module)) return;
        modules.add(module);
        module.onEnable();
        System.out.println("[ModuleManager] Registered module: " + module.info().name() + " (" + module.info().version() + ")");
    }

    public void unregisterModule(TwitchModule module) {
        modules.remove(module);
        module.onDisable();
        System.out.println("[ModuleManager] Unregistered module: " + module.info().name() + " (" + module.info().version() + ")");
    }

}
