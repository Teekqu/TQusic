package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.TwitchChannel;

public interface RewardRedeemedEvent {

    void onEvent(TwitchChannel channel, com.github.twitch4j.pubsub.events.RewardRedeemedEvent event);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(com.github.twitch4j.pubsub.events.RewardRedeemedEvent.class, e -> {
            TwitchChannel channel = CacheManager.get().getChannel(e.getRedemption().getChannelId());
            onEvent(channel, e);
        });
    }

}
