package eu.devload.twitch.modules.music.core.event;

import com.github.twitch4j.helix.domain.Stream;
import eu.devload.twitch.interfaces.TwitchLiveEvent;
import eu.devload.twitch.interfaces.TwitchOfflineEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.objects.TwitchChannel;

public class TwitchGoOffline implements TwitchOfflineEvent {
    @Override
    public void onChannelGoOffline(TwitchChannel channel) {

        CacheManager.get().removeLiveChannel(channel.id());

    }
}
