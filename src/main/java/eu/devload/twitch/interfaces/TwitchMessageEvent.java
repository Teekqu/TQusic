package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.Objects;

public interface TwitchMessageEvent {

    void onMessage(TwitchChannel channel, EventUser user, String message);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, e -> {
            if(!Objects.equals(e.getChannel().getId(), e.getSourceChannelId().orElse(e.getChannel().getId()))) return;
            if(e.getMessage().startsWith("!")) return;
            if(e.getChannel().getId().equals(ClientUser.get().id())) return;
            TwitchChannel channel = CacheManager.get().getChannel(e.getChannel().getId());
            onMessage(channel, e.getUser(), e.getMessage());
        });
    }

}
