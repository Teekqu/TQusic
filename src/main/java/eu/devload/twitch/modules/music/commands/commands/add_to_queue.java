package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;

public class add_to_queue implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("add_to_queue")) return;
        if(!channel.isModerator(sender.getId())) return;

        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if (args.length == 0) {
            channel.sendMessage("Please provide a song name or URL. | "+sender.getName());
            return;
        }

        String songName = String.join(" ", args);
        SpotifyTrack track = channel.spotifyApi().searchTrack(songName);
        if (track != null) {
            if(channel.spotifyApi().addTrack(track)) {
                String artistNames = track.artists().stream()
                        .map(artist -> artist.name())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Unknown");
                channel.sendMessage("Added " + track.name() + " by "+artistNames+" to the queue. | "+sender.getName());
            } else {
                channel.sendMessage("Could not add " + track.name() + " to the queue. | "+sender.getName());
            }
        } else {
            channel.sendMessage("Could not find the song: " + songName + " | "+sender.getName());
        }

    }
}
