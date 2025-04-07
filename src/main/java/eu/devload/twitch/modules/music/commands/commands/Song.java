package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.SpotifyAPI;
import eu.devload.twitch.objects.SpotifyArtist;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;

import javax.sound.midi.SysexMessage;

public class Song implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("song")) return;
        SpotifyAPI spotify = channel.spotifyApi();
        if (spotify == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        SpotifyTrack track = spotify.getCurrentPlaying();
        if (track == null) {
            channel.sendMessage("No song is currently playing. | "+sender.getName());
            return;
        }

        String artistNames = track.artists().stream()
                .map(SpotifyArtist::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown");
        channel.sendMessage("Now playing: " + track.name() + " by " + artistNames + " | "+sender.getName());

    }
}
