package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.TwitchChannel;

public interface TwitchOfflineEvent {

    void onChannelGoOffline(TwitchChannel channel);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelGoOfflineEvent.class, e -> {
            onChannelGoOffline(CacheManager.get().getChannel(e.getChannel().getId()));
        });
    }

}
