package eu.devload.twitch.utils;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import eu.devload.twitch.manager.ModuleManager;
import eu.devload.twitch.manager.TwitchManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

@Accessors(fluent = true)
@Getter
@Setter
public class SystemAPI {
    private static SystemAPI instance;

    public static SystemAPI get() {
        if (instance == null) instance = new SystemAPI();
        return instance;
    }


    private Config                  config;
    private Config                  oauthConfig;
    private Database                database;
    private TwitchClient            client;
    private TwitchManager           twitchManager;
    private SimpleEventHandler      eventHandler;
    private ModuleManager           moduleManager;

    public SystemAPI() {
        this.config = new Config();
        this.oauthConfig = new Config(new File("./oauth2.json"));
        this.database = new Database(
                this.config.get("database.ip").getAsString(),
                this.config.get("database.port").getAsInt(),
                this.config.get("database.database").getAsString(),
                this.config.get("database.username").getAsString(),
                this.config.get("database.password").getAsString(),
                this.config.get("database.maxConnections").getAsInt()
        ).connect();
    }

    public TwitchManager twitchManager() {
        if (this.twitchManager == null) this.twitchManager = new TwitchManager(this.client);
        return this.twitchManager;
    }

    public ModuleManager moduleManager() {
        if (this.moduleManager == null) this.moduleManager = new ModuleManager();
        return this.moduleManager;
    }
}
