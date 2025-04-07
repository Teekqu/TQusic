package eu.devload.twitch.manager;

import com.github.twitch4j.TwitchClient;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public record TwitchManager(TwitchClient client) {

    public List<TwitchChannel> registeredChannels() {
        try {
            List<TwitchChannel> channels = new ArrayList<>();

            ResultSet rsWt = SystemAPI.get().database().query("SELECT * FROM Channels ORDER BY channelId ASC;");

            while (rsWt.next()) {
                String channelId = rsWt.getString("channelId");
                TwitchChannel channel = CacheManager.get().getChannel(channelId);
                if(channel.exists()) channels.add(channel);
            }

            try { rsWt.close(); } catch (Exception ignored) { }
            return channels;
        } catch (Exception err) {
            err.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean removeChannel(TwitchChannel channel) {
        if(channel.id().equals(ClientUser.get().id())) return false;
        try {
            SystemAPI.get().database().execute("DELETE FROM Channels WHERE channelId='" + channel.id() + "';");
            this.leaveChannel(channel);
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public void joinChannels(List<TwitchChannel> channel) {
        channel.forEach(this::joinChannel);
    }

    public void joinChannel(TwitchChannel channel) {
        this.client.getChat().joinChannel(channel.getName());
        this.client.getClientHelper().enableStreamEventListener(channel.getName());
        System.out.println("[JOINED] " + channel.getName() + " (" + channel.id() + ")");
    }

    public void leaveChannel(TwitchChannel channel) {
        if(channel.id().equals(ClientUser.get().id())) return;
        this.client.getClientHelper().disableStreamEventListener(channel.getName());
        this.client.getChat().leaveChannel(channel.getName());
        System.out.println("[LEAVED] " + channel.getName() + " (" + channel.id() + ")");
    }

    public void leaveChannels(List<TwitchChannel> channel) {
        channel.forEach(this::leaveChannel);
    }

    public JSONObject generateOauth2FromCode(String userId) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + userId + "';");
            if (!rs.next()) {
                try {
                    rs.close();
                } catch (Exception ignored) {
                }
                return null;
            } else {
                String code = rs.getString("token");
                try {
                    rs.close();
                } catch (Exception ignored) {
                }
                SystemAPI.get().database().execute("DELETE FROM OauthTokens WHERE id='" + userId + "';");
                return this.generateNewOauth2TokenWithCode(code);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public JSONObject generateNewOauth2TokenWithCode(String code) {
        try {
            URL url = new URL(String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s", SystemAPI.get().config().get("twitch.client.id").getAsString(), SystemAPI.get().config().get("twitch.client.secret").getAsString(), code, SystemAPI.get().config().get("twitch.redirect.uri").getAsString()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            in.close();
            connection.disconnect();
            return new JSONObject(sb.toString());
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public JSONObject generateNewOauth2TokenWithRefreshToken(String refreshToken) {
        try {
            URL url = new URL("https://id.twitch.tv/oauth2/token?grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + SystemAPI.get().config().get("twitch.client.id").getAsString() + "&client_secret=" + SystemAPI.get().config().get("twitch.client.secret").getAsString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            in.close();
            connection.disconnect();
            return new JSONObject(sb.toString());
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

}
