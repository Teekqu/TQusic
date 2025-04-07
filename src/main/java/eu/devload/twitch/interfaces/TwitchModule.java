package eu.devload.twitch.interfaces;

import eu.devload.twitch.manager.CommandTriggerImpl;
import eu.devload.twitch.utils.SystemAPI;

public interface TwitchModule {

    void onEnable();
    void onDisable();
    void onReset();
    ModuleInfo info();

    default void registerCommand(TwitchCommand command) {
        CommandTriggerImpl.addCommand(command.getClass());
    }

    default void registerEvent(Object event) {
        if(event instanceof TwitchMessageEvent e) e.register(SystemAPI.get().eventHandler());
        else if(event instanceof RewardRedeemedEvent e) e.register(SystemAPI.get().eventHandler());
    }

}
