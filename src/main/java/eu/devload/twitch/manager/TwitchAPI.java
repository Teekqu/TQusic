package eu.devload.twitch.manager;

import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class TwitchAPI {

    public static Optional<String> createNewRewardForSongRequest(TwitchChannel channel) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + channel.id()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + channel.oauth2("t"));
            conn.setRequestProperty("client-id", SystemAPI.get().config().get("twitch.client.id").getAsString());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");

            JSONObject json = new JSONObject();
            json.put("title", "Song Request");
            json.put("cost", 300);
            json.put("prompt", "Request a song via spotify link or song name!");
            json.put("is_enabled", true);
            json.put("is_user_input_required", true);

            conn.getOutputStream().write(json.toString().getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // read response and parse to json object
                StringBuilder response = new StringBuilder();
                String line;
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("data")) {
                    JSONObject data = jsonResponse.getJSONArray("data").getJSONObject(0);
                    channel.setSongRequestId(data.getString("id"));
                    return Optional.empty();
                }
                return Optional.of("Err: No data found");
            }
            return Optional.of("Err: " + responseCode);

        } catch (Exception err) {
            err.printStackTrace();
            return Optional.of("Err: "+err.getMessage());
        }
    }

    public static Optional<String> createNewRewardForSongSkip(TwitchChannel channel) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.twitch.tv/helix/channel_points/custom_rewards?broadcaster_id=" + channel.id()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + channel.oauth2("t"));
            conn.setRequestProperty("client-id", SystemAPI.get().config().get("twitch.client.id").getAsString());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");

            JSONObject json = new JSONObject();
            json.put("title", "Song Skip");
            json.put("cost", 500);
            json.put("prompt", "Skip the current song!");
            json.put("is_enabled", true);
            json.put("is_user_input_required", true);

            conn.getOutputStream().write(json.toString().getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // read response and parse to json object
                StringBuilder response = new StringBuilder();
                String line;
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("data")) {
                    JSONObject data = jsonResponse.getJSONArray("data").getJSONObject(0);
                    channel.setSongSkipId(data.getString("id"));
                    return Optional.empty();
                }
                return Optional.of("Err: No data found");
            }
            return Optional.of("Err: " + responseCode);

        } catch (Exception err) {
            err.printStackTrace();
            return Optional.of("Err: "+err.getMessage());
        }
    }

}
