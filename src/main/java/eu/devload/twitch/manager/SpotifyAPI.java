package eu.devload.twitch.manager;

import eu.devload.twitch.objects.SpotifyArtist;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpotifyAPI {


    private static final String SPOTIFY_CLIENT_ID = "12fdbed4122e4d2a94307f1acfebefb4";
    private static final String SPOTIFY_CLIENT_SECRET = "2ed0df4f643241adab08b6e7514ca71e";


    public static SpotifyAPI get(TwitchChannel channel, String accessToken, String refreshToken, long expires) {
        return new SpotifyAPI(channel, accessToken, refreshToken, expires);
    }

    public static JSONObject generateNewAccessTokenByRefreshToken(String refreshToken) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://accounts.spotify.com/api/token").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET).getBytes()));
            String body = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + SPOTIFY_CLIENT_ID;
            conn.getOutputStream().write(body.getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return new JSONObject(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                return null;
            }

        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    private TwitchChannel twitch;
    private String accessToken;
    private String refreshToken;
    private long expires;

    public SpotifyAPI(TwitchChannel twitch, String accessToken, String refreshToken, long expires) {
        this.twitch = twitch;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expires = expires;
        this.checkAndRefresh();
    }

    public void checkAndRefresh() {
        if (System.currentTimeMillis() > expires) {
            JSONObject json = twitch.rawOauth2("s");
            json = twitch.rawOauth2("s");
            if (json != null) {
                this.accessToken = json.getString("access_token");
                this.refreshToken = json.getString("refresh_token");
                this.expires = System.currentTimeMillis()/1000 + json.getLong("expires");
            }
        }
    }


    public SpotifyTrack searchTrack(String search) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/search?q=" + search + "&type=track&limit=1").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();
                JSONObject json = new JSONObject(response.toString());
                if (json.has("tracks")) {
                    JSONObject tracks = json.getJSONObject("tracks");
                    if (tracks.has("items")) {
                        JSONObject item = tracks.getJSONArray("items").getJSONObject(0);
                        String id = item.getString("id");
                        String name = item.getString("name");
                        String href = item.getString("href");
                        String spotifyUrl = item.getJSONObject("external_urls").getString("spotify");
                        String uri = item.getString("uri");
                        long durationMs = item.getLong("duration_ms");
                        int popularity = item.getInt("popularity");
                        boolean isLocal = item.getBoolean("is_local");
                        List<SpotifyArtist> artists = new ArrayList<>();
                        if (item.has("artists")) {
                            for (int i = 0; i < item.getJSONArray("artists").length(); i++) {
                                JSONObject artist = item.getJSONArray("artists").getJSONObject(i);
                                String artistId = artist.getString("id");
                                String artistName = artist.getString("name");
                                String artistHref = artist.getString("href");
                                String artistSpotifyUrl = artist.getJSONObject("external_urls").getString("spotify");
                                String artistUri = artist.getString("uri");
                                artists.add(new SpotifyArtist(artistId, artistName, artistHref, artistSpotifyUrl, artistUri));
                            }
                        }
                        SpotifyTrack track = new SpotifyTrack(id, name, href, spotifyUrl, uri, durationMs, popularity, isLocal, artists);
                        return track;
                    }
                }
            }
            return null;

        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public SpotifyTrack getTrack(String id) {

        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/tracks/" + id).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();
                JSONObject json = new JSONObject(response.toString());
                if (json.has("id")) {
                    String id_ = json.getString("id");
                    String name = json.getString("name");
                    String href = json.getString("href");
                    String spotifyUrl = json.getJSONObject("external_urls").getString("spotify");
                    String uri = json.getString("uri");
                    long durationMs = json.getLong("duration_ms");
                    int popularity = json.getInt("popularity");
                    boolean isLocal = json.getBoolean("is_local");
                    List<SpotifyArtist> artists = new ArrayList<>();
                    if (json.has("artists")) {
                        for (int i = 0; i < json.getJSONArray("artists").length(); i++) {
                            JSONObject artist = json.getJSONArray("artists").getJSONObject(i);
                            String artistId = artist.getString("id");
                            String artistName = artist.getString("name");
                            String artistHref = artist.getString("href");
                            String artistSpotifyUrl = artist.getJSONObject("external_urls").getString("spotify");
                            String artistUri = artist.getString("uri");
                            artists.add(new SpotifyArtist(artistId, artistName, artistHref, artistSpotifyUrl, artistUri));
                        }
                    }
                    return new SpotifyTrack(id_, name, href, spotifyUrl, uri, durationMs, popularity, isLocal, artists);
                }
            }
            return null;

        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }

    public SpotifyTrack getCurrentPlaying() {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/currently-playing").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();
                JSONObject json = new JSONObject(response.toString());
                if (json.has("item")) {
                    JSONObject item = json.getJSONObject("item");
                    if(!item.has("type") || !item.getString("type").equalsIgnoreCase("track")) {
                        return null;
                    }
                    String id = item.getString("id");
                    String name = item.getString("name");
                    String href = item.getString("href");
                    String spotifyUrl = item.getJSONObject("external_urls").getString("spotify");
                    String uri = item.getString("uri");
                    long durationMs = item.getLong("duration_ms");
                    int popularity = item.getInt("popularity");
                    boolean isLocal = item.getBoolean("is_local");
                    List<SpotifyArtist> artists = new ArrayList<>();
                    if (item.has("artists")) {
                        for (int i = 0; i < item.getJSONArray("artists").length(); i++) {
                            JSONObject artist = item.getJSONArray("artists").getJSONObject(i);
                            String artistId = artist.getString("id");
                            String artistName = artist.getString("name");
                            String artistHref = artist.getString("href");
                            String artistSpotifyUrl = artist.getJSONObject("external_urls").getString("spotify");
                            String artistUri = artist.getString("uri");
                            artists.add(new SpotifyArtist(artistId, artistName, artistHref, artistSpotifyUrl, artistUri));
                        }
                    }
                    return new SpotifyTrack(id, name, href, spotifyUrl, uri, durationMs, popularity, isLocal, artists);
                }
            }
            return null;

        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public boolean start() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/play").openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 204) {
                return true;
            }
            return false;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean pause() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/pause").openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 204) {
                return true;
            }
            return false;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean skip() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/next").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            return responseCode == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean previous() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/previous").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            return responseCode == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean addTrack(SpotifyTrack track) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/queue?uri=" + track.uri()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            return responseCode == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean setVolume(int volume) {
        if(volume < 0 || volume > 100) {
            return false;
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player/volume?volume_percent=" + volume).openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            return responseCode == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public int getVolume() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.spotify.com/v1/me/player").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();
                JSONObject json = new JSONObject(response.toString());
                if (json.has("device")) {
                    return json.getJSONObject("device").getInt("volume_percent");
                }
            }
            return -1;
        } catch (Exception err) {
            err.printStackTrace();
            return -1;
        }
    }

}
