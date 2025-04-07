package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.TwitchChannel;

public class Pause implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!channel.isModerator(sender.getId())) return;

        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if (channel.spotifyApi().pause()) {
            channel.sendMessage("Paused the current track. | "+sender.getName());
        } else {
            channel.sendMessage("No song is currently playing. | "+sender.getName());
        }

    }
}
