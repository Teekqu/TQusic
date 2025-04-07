package eu.devload.twitch.manager;

import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class TwitchAPI {

    public static Optional<String> createNewRewardForSongRequest(TwitchChannel channel) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            JSONObject json = new JSONObject();
            json.put("title", "Song Request");
            json.put("cost", 300);
            json.put("prompt", "Request a song via spotify link or song name!");
            json.put("is_enabled", true);
            json.put("is_user_input_required", true);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + channel.id()))
                    .header("Authorization", "Bearer " + channel.oauth2("t"))
                    .header("Client-Id", SystemAPI.get().config().get("twitch.client.id").getAsString())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("data")) {
                    JSONObject data = jsonResponse.getJSONArray("data").getJSONObject(0);
                    channel.setSongRequestId(data.getString("id"));
                    return Optional.empty();
                }
                return Optional.of("Err: No data found");
            }
            return Optional.of("Err: " + response.statusCode());

        } catch (Exception err) {
            err.printStackTrace();
            return Optional.of("Err: "+err.getMessage());
        }
    }

    public static Optional<String> createNewRewardForSongSkip(TwitchChannel channel) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject json = new JSONObject();
            json.put("title", "Song Skip");
            json.put("cost", 500);
            json.put("prompt", "Skip the current song!");
            json.put("is_enabled", true);
            json.put("is_user_input_required", false);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + channel.id()))
                    .header("Authorization", "Bearer " + channel.oauth2("t"))
                    .header("Client-Id", SystemAPI.get().config().get("twitch.client.id").getAsString())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("data")) {
                    JSONObject data = jsonResponse.getJSONArray("data").getJSONObject(0);
                    channel.setSongSkipId(data.getString("id"));
                    return Optional.empty();
                }
                return Optional.of("Err: No data found");
            }
            return Optional.of("Err: " + response.statusCode());

        } catch (Exception err) {
            err.printStackTrace();
            return Optional.of("Err: "+err.getMessage());
        }
    }

}
