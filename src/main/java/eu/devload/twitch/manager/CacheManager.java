package eu.devload.twitch.manager;

import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.objects.UserObject;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;

public class CacheManager {

    private static CacheManager instance;
    public static CacheManager get() {
        if(instance == null) instance = new CacheManager();
        return instance;
    }

    private HashMap<String, TwitchChannel> channels = new HashMap<>();
    private HashMap<String, LiveObject> liveChannels = new HashMap<>();
    private HashMap<String, UserObject> users = new HashMap<>();

    public void setChannel(TwitchChannel twitchChannel) {
        if(channels.containsKey(twitchChannel.id())) channels.replace(twitchChannel.id(), twitchChannel);
        else channels.put(twitchChannel.id(), twitchChannel);
    }
    public TwitchChannel getChannel(String id) {
        if(!channels.containsKey(id)) channels.put(id, new TwitchChannel(id));
        return channels.get(id);
    }
    public void removeChannel(String id) {
        channels.remove(id);
    }

    public void setLiveChannel(LiveObject liveObject) {
        if(liveChannels.containsKey(liveObject.channelId())) liveChannels.replace(liveObject.channelId(), liveObject);
        else liveChannels.put(liveObject.channelId(), liveObject);
    }
    public LiveObject getLiveChannel(String id) {
        return liveChannels.get(id);
    }
    public void removeLiveChannel(String id) {
        liveChannels.remove(id);
    }

    public void setUser(UserObject user) {
        try {
            if(user.broadcasterType().isEmpty()) user.broadcasterType("default");
            if(user.description().isEmpty()) user.description("No description provided.");
            if(user.profileImageUrl().isEmpty()) user.profileImageUrl("https://i.devload.eu/empty-profile-picture.png");
            SystemAPI.get().database().execute(
                    "INSERT INTO UserCache(id, login, displayName, broadcasterType, description, profileImageUrl, createdAt) VALUES('" + user.id() + "', '" + user.login() + "', '" + user.displayName() + "', '" + user.broadcasterType() + "', \"" + user.description() + "\", '" + user.profileImageUrl() + "', " + user.createdAt() + ") " +
                            "ON DUPLICATE KEY UPDATE login='" + user.login() + "', displayName='" + user.displayName() + "', broadcasterType='" + user.broadcasterType() + "', description=\"" + user.description() + "\", profileImageUrl='" + user.profileImageUrl() + "', createdAt=" + user.createdAt()
            );
            if(users.containsKey(user.id())) users.replace(user.id(), user);
            else users.put(user.id(), user);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    public UserObject getUserById(String id) {
        if(users.containsKey(id)) return users.get(id);
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM UserCache WHERE id='" + id + "'");
            if(!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                TwitchChannel ch = SystemAPI.get().twitchManager().registeredChannels().getFirst();
                User user = SystemAPI.get().client().getHelix().getUsers(ch.oauth2("t"), Collections.singletonList(id), null).execute().getUsers().getFirst();
                if(user == null) return null;
                UserObject userObject = new UserObject(
                        user.getId(),
                        user.getLogin(),
                        user.getDisplayName(),
                        user.getBroadcasterType(),
                        user.getDescription(),
                        user.getProfileImageUrl(),
                        user.getCreatedAt().getEpochSecond()
                );
                this.setUser(userObject);
                return userObject;
            }
            UserObject user = new UserObject(rs.getString("id"), rs.getString("login"), rs.getString("displayName"), rs.getString("broadcasterType"), rs.getString("description"), rs.getString("profileImageUrl"), rs.getLong("createdAt"));
            users.put(user.id(), user);
            try { rs.close(); } catch (Exception ignored) { }
            return user;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
    public UserObject getUserByName(String name) {
        if(name.startsWith("@")) name = name.substring(1);
        for(UserObject user : users.values()) if(user.login().equalsIgnoreCase(name)) return user;
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM UserCache WHERE login='" + name.toLowerCase() + "'");
            if(!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                TwitchChannel ch = SystemAPI.get().twitchManager().registeredChannels().getFirst();
                User user = SystemAPI.get().client().getHelix().getUsers(ch.oauth2("t"), null, Collections.singletonList(name)).execute().getUsers().getFirst();
                if(user == null) return null;
                UserObject userObject = new UserObject(
                        user.getId(),
                        user.getLogin(),
                        user.getDisplayName(),
                        user.getBroadcasterType(),
                        user.getDescription(),
                        user.getProfileImageUrl(),
                        user.getCreatedAt().getEpochSecond()
                );
                this.setUser(userObject);
                return null;
            }
            UserObject user = new UserObject(rs.getString("id"), rs.getString("login"), rs.getString("displayName"), rs.getString("broadcasterType"), rs.getString("description"), rs.getString("profileImageUrl"), rs.getLong("createdAt"));
            users.put(user.id(), user);
            try { rs.close(); } catch (Exception ignored) { }
            return user;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
    public void removeUser(String id) {
        users.remove(id);
        try {
            SystemAPI.get().database().execute("DELETE FROM UserCache WHERE id='" + id + "'");
        } catch (Exception err) {
            err.printStackTrace();
        }
        users.remove(id);
    }

    public void initialUserCache() {
        System.out.println("Loading UserCache...");
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM UserCache");
            while(rs.next()) {
                UserObject user = new UserObject(rs.getString("id"), rs.getString("login"), rs.getString("displayName"), rs.getString("broadcasterType"), rs.getString("description"), rs.getString("profileImageUrl"), rs.getLong("createdAt"));
                if(!users.containsKey(user.id())) users.put(user.id(), user);
                else users.replace(user.id(), user);
            }
            try { rs.close(); } catch (Exception ignored) { }
            System.out.println("Finished loading UserCache.");
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("Failed to load UserCache.");
        }
    }

}
