package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.SpotifyAPI;
import eu.devload.twitch.objects.TwitchChannel;

public class Previous implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("previous")) return;
        if(!channel.isModerator(sender.getId())) return;

        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if(!channel.isLive()) {
            channel.sendMessage("This command is only available when the channel is live. | " + sender.getName());
            return;
        }

        if (channel.spotifyApi().previous()) {
            channel.sendMessage("Skipped to the previous track. | "+sender.getName());
        } else {
            channel.sendMessage("No song is currently playing. | "+sender.getName());
        }

    }
}
