package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.TwitchChannel;

public class Skip implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("skip")) return;
        if(!channel.isModerator(sender.getId())) return;

        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if(!channel.isLive()) {
            channel.sendMessage("This command is only available when the channel is live. | " + sender.getName());
            return;
        }

        if (channel.spotifyApi().skip()) {
            channel.sendMessage("Skipped the current track. | "+sender.getName());
        } else {
            channel.sendMessage("No song is currently playing. | "+sender.getName());
        }

    }
}
