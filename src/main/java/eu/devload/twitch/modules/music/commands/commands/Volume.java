package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.SpotifyAPI;
import eu.devload.twitch.objects.TwitchChannel;

public class Volume implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!channel.isModerator(sender.getId())) return;

        SpotifyAPI spotify = channel.spotifyApi();
        if (spotify == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if (args.length == 0) {
            channel.sendMessage("Current volume: " + spotify.getVolume() + "% | "+sender.getName());
            return;
        }

        try {
            int volume = Integer.parseInt(args[0]);
            if (volume < 0 || volume > 100) {
                channel.sendMessage("Volume must be between 0 and 100. | "+sender.getName());
                return;
            }
            if(spotify.setVolume(volume)) {
                channel.sendMessage("Volume set to " + volume + "% | "+sender.getName());
            } else {
                channel.sendMessage("Failed to set volume. | "+sender.getName());
            }
        } catch (NumberFormatException e) {
            channel.sendMessage("Invalid volume value. Please enter a number between 0 and 100. | "+sender.getName());
        }

    }
}
