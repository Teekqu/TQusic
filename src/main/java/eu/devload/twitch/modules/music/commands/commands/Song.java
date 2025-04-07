package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.SpotifyAPI;
import eu.devload.twitch.objects.SpotifyArtist;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;

public class Song implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("song")) return;
        System.out.println("!song command executed by " + sender.getName());
        SpotifyAPI spotify = channel.spotifyApi();
        if (spotify == null) {
            System.out.println("1");
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        System.out.println("2");
        SpotifyTrack track = spotify.getCurrentPlaying();
        System.out.println("3");
        if (track == null) {
            System.out.println("4");
            channel.sendMessage("No song is currently playing. | "+sender.getName());
            return;
        }
        System.out.println("5");

        String artistNames = track.artists().stream()
                .map(SpotifyArtist::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown");
        System.out.println("6");
        System.out.println(track.name() + " by " + artistNames);
        channel.sendMessage("Now playing: " + track.name() + " by " + artistNames + " | "+sender.getName());

    }
}
