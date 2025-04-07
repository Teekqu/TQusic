package eu.devload.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import eu.devload.twitch.api.API;
import eu.devload.twitch.manager.CommandTriggerImpl;
import eu.devload.twitch.manager.ModuleManager;
import eu.devload.twitch.modules.music.commands.MusicCommandModule;
import eu.devload.twitch.modules.music.points.MusicPointsModule;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.utils.SystemAPI;

public class TQusic {

    public static void main(String[] args) {
        TwitchClient client = TwitchClientBuilder.builder()
                .withDefaultAuthToken(new OAuth2Credential("twitch", SystemAPI.get().config().get("twitch.client.oauth").getAsString()))
                .withEnableHelix(true)
                .withEnablePubSub(true)
                .withChatQueueTimeout(10000)
                .withTimeout(10000)
                .withEnableChat(true)
                .withChatAccount(new OAuth2Credential("twitch", SystemAPI.get().config().get("twitch.client.oauth").getAsString()))
                .withChatCommandsViaHelix(true)
                .build();

        SimpleEventHandler eventHandler = client.getEventManager().getEventHandler(SimpleEventHandler.class);
        SystemAPI.get().client(client);
        SystemAPI.get().eventHandler(eventHandler);

        // Modules
        ModuleManager moduleManager = SystemAPI.get().moduleManager();
        moduleManager.registerModule(new MusicCommandModule());
        moduleManager.registerModule(new MusicPointsModule());

        // API
        new API();

        SystemAPI.get().twitchManager().joinChannels(SystemAPI.get().twitchManager().registeredChannels());
        client.getChat().joinChannel(ClientUser.get().login());
        client.getChat().connect();

        CommandTriggerImpl.startEventCheck();
    }

}
