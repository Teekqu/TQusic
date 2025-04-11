package eu.devload.twitch.modules.music.points.events;

import eu.devload.twitch.interfaces.RewardRedeemedEvent;
import eu.devload.twitch.manager.TwitchAPI;
import eu.devload.twitch.objects.SpotifyArtist;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;

public class ChannelRewardClaimed implements RewardRedeemedEvent {
    @Override
    public void onEvent(TwitchChannel channel, com.github.twitch4j.pubsub.events.RewardRedeemedEvent event) {
        if(event.getRedemption().getReward().getId().equalsIgnoreCase(channel.getSongRequestId())) {
            if(channel.spotifyApi() == null) {
                channel.sendMessage("Spotify API is not available. | " + event.getRedemption().getUser().getDisplayName());
                return;
            }

            if(!channel.isLive()) {
                channel.sendMessage("Song requests are only available when the channel is live. | " + event.getRedemption().getUser().getDisplayName());
                TwitchAPI.cancelReward(channel, event.getRedemption().getId());
                return;
            }

            String input = event.getRedemption().getUserInput();
            if (input != null && !input.isEmpty()) {
                SpotifyTrack track = channel.spotifyApi().searchTrack(input);
                if(track == null) {
                    channel.sendMessage("No song found for: " + input + " | " + event.getRedemption().getUser().getDisplayName());
                    TwitchAPI.cancelReward(channel, event.getRedemption().getId());
                    return;
                }
                channel.spotifyApi().addTrack(track);
                String artistNames = track.artists().stream()
                        .map(SpotifyArtist::name)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Unknown");
                channel.sendMessage("Added song to queue: " + input + " by "+artistNames+" | " + event.getRedemption().getUser().getDisplayName());
            } else {
                channel.sendMessage("No song name provided. | " + event.getRedemption().getUser().getDisplayName());
                TwitchAPI.cancelReward(channel, event.getRedemption().getId());
            }
        } else if(event.getRedemption().getReward().getId().equalsIgnoreCase(channel.getSongSkipId())) {
            if(channel.spotifyApi() == null) {
                channel.sendMessage("Spotify API is not available. | " + event.getRedemption().getUser().getDisplayName());
                TwitchAPI.cancelReward(channel, event.getRedemption().getId());
                return;
            }
            if(channel.spotifyApi().skip()) {
                channel.sendMessage("Skipped song with channel points | " + event.getRedemption().getUser().getDisplayName());
            } else {
                channel.sendMessage("No song to skip | " + event.getRedemption().getUser().getDisplayName());
                TwitchAPI.cancelReward(channel, event.getRedemption().getId());
            }
        }

    }
}
