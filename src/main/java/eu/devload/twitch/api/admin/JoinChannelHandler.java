package eu.devload.twitch.api.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

public class JoinChannelHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if(auth == null || !auth.equals("DevLoad q057ht9&Qw7tq597hgq73%/hrsegae9urghf34")) {
                System.out.println("[API] Unauthorized request");
                exchange.sendResponseHeaders(401, 0);
                exchange.getResponseBody().close();
                return;
            }

            System.out.println("0");
            String[] split = exchange.getRequestURI().getQuery().split("=");
            String channel = split[1];

            System.out.println("0.5");
            // read body and parse it as json object
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("0.6");
            System.out.println(body);
            JSONObject obj = new JSONObject(body);
            String tAccessToken = obj.getJSONObject("twitch").getString("access_token");
            System.out.println("1.1");
            String tRefreshToken = obj.getJSONObject("twitch").getString("refresh_token");
            System.out.println("1.2");
            long tExpiresIn = obj.getLong("tOauthTime") + obj.getJSONObject("twitch").getLong("expires_in");
            System.out.println("1.3");
            String sAccessToken = obj.getJSONObject("spotify").getString("access_token");
            System.out.println("1.4");
            String sRefreshToken = obj.getJSONObject("spotify").getString("refresh_token");
            System.out.println("1.5");
            long sExpiresIn = obj.getLong("sOauthTime") + obj.getJSONObject("spotify").getLong("expires_in");

            System.out.println("1");
            if(channel == null || channel.isEmpty() || channel.equalsIgnoreCase(ClientUser.get().id())) {
                System.out.println("[API] Invalid channel");
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().close();
                return;
            }

            System.out.println("2");

            try {
                System.out.println("3");
                SystemAPI.get().database().execute("INSERT INTO Channels(channelId, tAccessToken, tRefreshToken, tExpires, sAccessToken, sRefreshToken, sExpires) VALUES('" + channel + "', '" + tAccessToken + "', '" + tRefreshToken + "', '" + tExpiresIn + "', '" + sAccessToken + "', '" + sRefreshToken + "', '" + sExpiresIn + "') ON DUPLICATE KEY UPDATE tAccessToken='" + tAccessToken + "', tRefreshToken='" + tRefreshToken + "', tExpires='" + tExpiresIn + "', sAccessToken='" + sAccessToken + "', sRefreshToken='" + sRefreshToken + "', sExpires='" + sExpiresIn + "'");
                System.out.println("4");
                SystemAPI.get().twitchManager().joinChannel(new TwitchChannel(channel));
                System.out.println("5");
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
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
