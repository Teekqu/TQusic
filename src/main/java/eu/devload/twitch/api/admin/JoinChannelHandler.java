package eu.devload.twitch.api.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JoinChannelHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            String authToken = SystemAPI.get().config().get("api.auth.token").getAsString();
            if(auth == null || !auth.equals("DevLoad "+authToken)) {
                System.out.println("[API] Unauthorized request");
                exchange.sendResponseHeaders(401, 0);
                exchange.getResponseBody().close();
                return;
            }

            String[] split = exchange.getRequestURI().getQuery().split("=");
            String channel = split[1];

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(body);
            String tAccessToken = obj.getJSONObject("twitch").getString("access_token");
            String tRefreshToken = obj.getJSONObject("twitch").getString("refresh_token");
            long tExpiresIn = obj.getLong("tOauthTime") + obj.getJSONObject("twitch").getLong("expires_in");
            String sAccessToken = obj.getJSONObject("spotify").getString("access_token");
            String sRefreshToken = obj.getJSONObject("spotify").getString("refresh_token");
            long sExpiresIn = obj.getLong("sOauthTime") + obj.getJSONObject("spotify").getLong("expires_in");

            try {
                if(channel == null || channel.isEmpty() || channel.equalsIgnoreCase(ClientUser.get().id())) {
                    if(channel != null && channel.equalsIgnoreCase(ClientUser.get().id())) {
                        SystemAPI.get().database().execute("INSERT INTO Channels(channelId, tAccessToken, tRefreshToken, tExpires, sAccessToken, sRefreshToken, sExpires) VALUES('" + channel + "', '" + tAccessToken + "', '" + tRefreshToken + "', '" + tExpiresIn + "', '" + sAccessToken + "', '" + sRefreshToken + "', '" + sExpiresIn + "') ON DUPLICATE KEY UPDATE tAccessToken='" + tAccessToken + "', tRefreshToken='" + tRefreshToken + "', tExpires='" + tExpiresIn + "', sAccessToken='" + sAccessToken + "', sRefreshToken='" + sRefreshToken + "', sExpires='" + sExpiresIn + "'");
                    }
                    System.out.println("[API] Invalid channel");
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().close();
                    return;
                }

                SystemAPI.get().database().execute("INSERT INTO Channels(channelId, tAccessToken, tRefreshToken, tExpires, sAccessToken, sRefreshToken, sExpires) VALUES('" + channel + "', '" + tAccessToken + "', '" + tRefreshToken + "', '" + tExpiresIn + "', '" + sAccessToken + "', '" + sRefreshToken + "', '" + sExpiresIn + "') ON DUPLICATE KEY UPDATE tAccessToken='" + tAccessToken + "', tRefreshToken='" + tRefreshToken + "', tExpires='" + tExpiresIn + "', sAccessToken='" + sAccessToken + "', sRefreshToken='" + sRefreshToken + "', sExpires='" + sExpiresIn + "'");

                TwitchChannel tChannel = new TwitchChannel(channel);
                SystemAPI.get().twitchManager().joinChannel(tChannel);
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                if(!tChannel.getUptime().equalsIgnoreCase("-1s")) {
                    LiveObject liveObject = new LiveObject(tChannel.id(), "Unknown", "Unknown", 0);
                    CacheManager.get().setLiveChannel(liveObject);
                }
                System.out.println("[API] Joined channel " + channel);
            } catch (Exception err) {
                err.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
                System.out.println("[API] Error while trying to join channel " + channel);
            }

        }

    }
}
