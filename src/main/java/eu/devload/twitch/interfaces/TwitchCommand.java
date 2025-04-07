package eu.devload.twitch.interfaces;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.objects.TwitchChannel;

public interface TwitchCommand {

    void execute(TwitchChannel channel, EventUser sender, String command, String[] args);
    default void execute(TwitchChannel channel, EventUser sender, String command) {
        execute(channel, sender, command, new String[0]);
    }

}
