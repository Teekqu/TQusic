package eu.devload.twitch.modules.music.commands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.TwitchAPI;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.Optional;

public class GenerateSSkip implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("generate-sskip")) return;
        if(!channel.isBroadcaster(sender.getId())) return;
        if (channel.spotifyApi() == null) {
            channel.sendMessage("Spotify API is not available. | "+sender.getName());
            return;
        }

        if(channel.getBroadcasterType().equalsIgnoreCase("default")) {
            channel.sendMessage("You need to be a Twitch Partner or affiliate to use this command. | "+sender.getName());
            return;
        }

        Optional<String> reward = TwitchAPI.createNewRewardForSongSkip(channel);
        if(reward.isPresent() && !reward.get().isEmpty()) {
            channel.sendMessage("Error while creating reward - Make sure that you haven't created a reward with the same name before. | "+sender.getName());
            return;
        }
        channel.sendMessage("Created a new reward for song skip. You can edit the reward via twitch dashboard. | "+sender.getName());

    }
}
