package eu.devload.twitch.objects;

import com.github.twitch4j.common.enums.AnnouncementColor;
import com.github.twitch4j.helix.domain.*;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.manager.SpotifyAPI;
import eu.devload.twitch.utils.Convert;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchChannel {

    private final String id;

    public TwitchChannel(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public JSONObject rawOauth2(String prefix) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Channels WHERE channelId='" + id + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                SystemAPI.get().twitchManager().leaveChannel(this);
                return null;
            }
            String accessToken = rs.getString(prefix+"AccessToken");
            String refreshToken = rs.getString(prefix+"RefreshToken");
            long expires = rs.getLong(prefix+"Expires");
            try { rs.close(); } catch (Exception ignored) { }

            if (System.currentTimeMillis() / 1000 > expires) {
                if(prefix.equalsIgnoreCase("t")) {
                    JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                    if (json == null) {
                        SystemAPI.get().twitchManager().removeChannel(this);
                        return null;
                    }
                    String newOauth2 = json.getString("access_token");
                    String newRefreshToken = json.getString("refresh_token");
                    long expiresIn = System.currentTimeMillis()/1000 + json.getLong("expires_in");
                    SystemAPI.get().database().execute("UPDATE Channels SET tAccessToken='" + newOauth2 + "', tRefreshToken='" + newRefreshToken + "', tExpires=" + expiresIn + " WHERE channelId=" + id);
                    return json;
                } else {
                    JSONObject json = SpotifyAPI.generateNewAccessTokenByRefreshToken(refreshToken);
                    if (json == null) {
                        SystemAPI.get().twitchManager().removeChannel(this);
                        return null;
                    }
                    String newOauth2 = json.getString("access_token");
                    String newRefreshToken = json.getString("refresh_token");
                    long expiresIn = System.currentTimeMillis()/1000 + json.getLong("expires_in");
                    SystemAPI.get().database().execute("UPDATE Channels SET sAccessToken='" + newOauth2 + "', sRefreshToken='" + newRefreshToken + "', sExpires=" + expiresIn + " WHERE channelId=" + id);
                    return json;
                }
            }
            return new JSONObject().put("access_token", accessToken).put("refresh_token", refreshToken).put("expires", expires);
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public String oauth2(String prefix) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Channels WHERE channelId='" + id + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                SystemAPI.get().twitchManager().leaveChannel(this);
                return null;
            }
            String accessToken = rs.getString(prefix+"AccessToken");
            String refreshToken = rs.getString(prefix+"RefreshToken");
            long expires = rs.getLong(prefix+"Expires");
            try { rs.close(); } catch (Exception ignored) { }

            String oauth2;
            if (System.currentTimeMillis() / 1000 > expires) {
                if(prefix.equalsIgnoreCase("t")) {
                    JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                    if (json == null) {
                        SystemAPI.get().twitchManager().removeChannel(this);
                        return null;
                    }
                    String newOauth2 = json.getString("access_token");
                    String newRefreshToken = json.getString("refresh_token");
                    long expiresIn = System.currentTimeMillis()/1000 + json.getLong("expires_in");
                    SystemAPI.get().database().execute("UPDATE Channels SET tAccessToken='" + newOauth2 + "', tRefreshToken='" + newRefreshToken + "', tExpires=" + expiresIn + " WHERE channelId=" + id);
                    oauth2 = newOauth2;
                    return oauth2;
                } else {
                    JSONObject json = SpotifyAPI.generateNewAccessTokenByRefreshToken(refreshToken);
                    if (json == null) {
                        SystemAPI.get().twitchManager().removeChannel(this);
                        return null;
                    }
                    String newOauth2 = json.getString("access_token");
                    String newRefreshToken = json.getString("refresh_token");
                    long expiresIn = System.currentTimeMillis()/1000 + json.getLong("expires_in");
                    SystemAPI.get().database().execute("UPDATE Channels SET sAccessToken='" + newOauth2 + "', sRefreshToken='" + newRefreshToken + "', sExpires=" + expiresIn + " WHERE channelId=" + id);
                    oauth2 = newOauth2;
                    return oauth2;
                }
            }
            oauth2 = accessToken;
            return oauth2;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public SpotifyAPI spotifyApi() {
        JSONObject oauth = this.rawOauth2("s");
        return SpotifyAPI.get(this, oauth.getString("access_token"), oauth.getString("refresh_token"), oauth.getLong("expires"));
    }

    public boolean exists() {
        return this.oauth2("t") != null;
    }

    public UserObject getUser() {
        return CacheManager.get().getUserById(this.id);
    }

    public String getName() {
        return this.getUser().login();
    }

    public String getDisplayName() {
        return this.getUser().displayName();
    }

    public String getProfileImageUrl() {
        return this.getUser().profileImageUrl();
    }

    public String getBroadcasterType() {
        return this.getUser().broadcasterType();
    }

    public String getDescription() {
        return this.getUser().description();
    }

    public int getSubCount() {
        return (SystemAPI.get().client().getHelix().getSubscriptions(this.oauth2("t"), this.id, null, null, null).execute()).getTotal();
    }

    public int getFollowers() {
        return (SystemAPI.get().client().getHelix().getChannelFollowers(this.oauth2("t"), this.id, null, null, null).execute()).getTotal();
    }

    public String getUptime() {
        StreamList streams = SystemAPI.get().client().getHelix().getStreams(this.oauth2("t"), null, null, 1, null, null, Collections.singletonList(this.id), null).execute();
        return streams != null && !streams.getStreams().isEmpty() ? Convert.secondsToFormat((streams.getStreams().getFirst()).getUptime().toSeconds()) : "-1s";
    }

    public boolean isBroadcaster(String userId) {
        return this.id.equals(userId);
    }

    public boolean isModerator(String userId) {
        if (this.isBroadcaster(userId)) return true;
        List<Moderator> mods = (SystemAPI.get().client().getHelix().getModerators(this.oauth2("t"), this.id, null, null, null).execute()).getModerators();
        return mods.stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });
    }

    public boolean isEditor(String userId) {
        if (this.isBroadcaster(userId)) return true;

        ChannelEditorList editors = SystemAPI.get().client().getHelix().getChannelEditors(this.oauth2("t"), this.id).execute();
        return editors != null && !editors.getEditors().isEmpty() && editors.getEditors().stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });

    }

    public boolean isSubscriber(String userId) {
        if (this.isBroadcaster(userId)) return true;

        SubscriptionList subs = SystemAPI.get().client().getHelix().getSubscriptions(this.oauth2("t"), this.id, null, null, null).execute();
        return subs != null && !subs.getSubscriptions().isEmpty() && subs.getSubscriptions().stream().anyMatch((e) -> {
            return e.getUserId().equals(userId);
        });
    }

    public String getFollowAge(String userId) {
        if (this.id.equals(userId))
            return Convert.secondsToFormat(System.currentTimeMillis() / 1000L - this.getUser().createdAt());
        InboundFollowers follows = SystemAPI.get().client().getHelix().getChannelFollowers(this.oauth2("t"), this.id, userId, null, null).execute();
        return follows != null && follows.getFollows() != null && !follows.getFollows().isEmpty() ? Convert.secondsToFormat(System.currentTimeMillis() / 1000L - (follows.getFollows().getFirst()).getFollowedAt().getEpochSecond()) : "-1";
    }

    public boolean isLive() {
        return !this.getUptime().equals("-1s");
    }

    public Stream getStream() {
        StreamList streams = SystemAPI.get().client().getHelix().getStreams(this.oauth2("t"), null, null, 1, null, null, Collections.singletonList(this.id), null).execute();
        return streams != null && !streams.getStreams().isEmpty() ? streams.getStreams().getFirst() : null;
    }

    public void sendMessage(String message) {
//        ChatMessage msg = new ChatMessage(this.id(), ClientUser.get().id(), message, null);
//        SystemAPI.get().client().getHelix().sendChatMessage(ClientUser.get().oauth(), msg).execute();
        SystemAPI.get().client().getChat().sendMessage(this.getName(), message);
    }

    public void timeoutUser(String userId, int seconds, String reason) {
        CompletableFuture.runAsync(() -> {
            if(this.isBroadcaster(userId)) return;
            if(this.isEditor(userId)) return;
            if(this.isModerator(userId)) return;
            BanUserInput banUserInput = BanUserInput.builder().userId(userId).reason(reason).duration(seconds).build();
            SystemAPI.get().client().getHelix().banUser(ClientUser.get().oauth(), this.id, ClientUser.get().id(), banUserInput).execute();
        });
    }

    public void banUser(String userId, String reason) {
        CompletableFuture.runAsync(() -> {
            BanUserInput banUserInput = BanUserInput.builder().userId(userId).reason(reason).build();
            SystemAPI.get().client().getHelix().banUser(ClientUser.get().oauth(), this.id, ClientUser.get().id(), banUserInput).execute();
        });
    }

    public void unbanUser(String userId) {
        CompletableFuture.runAsync(() -> {
            SystemAPI.get().client().getHelix().unbanUser(ClientUser.get().oauth(), this.id, ClientUser.get().id(), userId).execute();
        });
    }

    public void setTitle(String title) {
        CompletableFuture.runAsync(() -> {
            ChannelInformation channelInformation = ChannelInformation.builder().title(title).build();
            SystemAPI.get().client().getHelix().updateChannelInformation(this.oauth2("t"), this.id, channelInformation).execute();
        });
    }

    public void setGame(String game) {
        CompletableFuture.runAsync(() -> {
            ChannelInformation channelInformation = ChannelInformation.builder().gameName(game).build();
            SystemAPI.get().client().getHelix().updateChannelInformation(this.oauth2("t"), this.id, channelInformation).execute();
        });
    }

    public void sendAnnouncement(String message) {
        CompletableFuture.runAsync(() -> {
            SystemAPI.get().client().getHelix().sendChatAnnouncement(ClientUser.get().oauth(), this.id, ClientUser.get().id(), message, AnnouncementColor.PRIMARY).execute();
        });
    }

    public String getSongRequestId() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Rewards WHERE channelId='"+id+"' AND type='songrequest';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return null;
            }
            String id = rs.getString("id");
            try { rs.close(); } catch (Exception ignored) { }
            return id;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public String getSongSkipId() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Rewards WHERE channelId='"+id+"' AND type='songskip';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return null;
            }
            String id = rs.getString("id");
            try { rs.close(); } catch (Exception ignored) { }
            return id;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public void setSongRequestId(String id) {
        try {
            SystemAPI.get().database().execute("INSERT INTO Rewards (channelId, type, id) VALUES ('"+this.id()+"', 'songrequest', '"+id+"') ON DUPLICATE KEY UPDATE id='"+id+"';");
            return;
        } catch (Exception err) {
            err.printStackTrace();
            return;
        }
    }
    public void setSongSkipId(String id) {
        try {
            SystemAPI.get().database().execute("INSERT INTO Rewards (channelId, type, id) VALUES ('"+this.id()+"', 'songskip', '"+id+"') ON DUPLICATE KEY UPDATE id='"+id+"';");
            return;
        } catch (Exception err) {
            err.printStackTrace();
            return;
        }
    }

    public boolean leave() {
        return SystemAPI.get().twitchManager().removeChannel(this);
    }

}
