package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Stream;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.Objects;

public interface TwitchLiveEvent {

    void onChannelGoLive(TwitchChannel channel, Stream stream);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelGoLiveEvent.class, e -> {
            onChannelGoLive(CacheManager.get().getChannel(e.getChannel().getId()), e.getStream());
        });
    }

}
