package eu.devload.twitch.modules.music.core.event;

import com.github.twitch4j.helix.domain.Stream;
import eu.devload.twitch.interfaces.TwitchLiveEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.objects.TwitchChannel;

public class TwitchGoLive implements TwitchLiveEvent {
    @Override
    public void onChannelGoLive(TwitchChannel channel, Stream stream) {

        LiveObject liveObject = new LiveObject(channel.id(), stream.getTitle(), stream.getGameName(), 0);
        CacheManager.get().setLiveChannel(liveObject);

    }
}
