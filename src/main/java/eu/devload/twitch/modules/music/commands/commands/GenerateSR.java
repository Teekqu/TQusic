package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.TwitchAPI;
import eu.devload.twitch.objects.TwitchChannel;

public class GenerateSR implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!channel.isBroadcaster(sender.getId())) return;

        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        TwitchAPI.createNewRewardForSongRequest(channel);
        channel.sendMessage("Created a new reward for song requests. You can edit the reward via twitch dashboard. | "+sender.getName());

    }
}
