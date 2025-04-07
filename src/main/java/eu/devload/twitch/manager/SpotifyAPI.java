package eu.devload.twitch.manager;

import eu.devload.twitch.objects.SpotifyArtist;
import eu.devload.twitch.objects.SpotifyTrack;
import eu.devload.twitch.objects.TwitchChannel;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://accounts.spotify.com/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "+java.util.Base64.getEncoder().encodeToString((SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET).getBytes()))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + SPOTIFY_CLIENT_ID))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JSONObject(response.body());
            } else {
                System.out.println("Error: " + response.statusCode());
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

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/search?q=" + search + "&type=track&limit=1"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
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
                        return new SpotifyTrack(id, name, href, spotifyUrl, uri, durationMs, popularity, isLocal, artists);
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/tracks/" + id))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/currently-playing"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                JSONObject json = new JSONObject(body);
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/play"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean pause() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/pause"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean skip() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/next"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            System.out.println(response.statusCode());
            return response.statusCode() == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean previous() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/previous"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean addTrack(SpotifyTrack track) {
        if(track == null) return false;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/queue?uri=" + track.uri()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player/volume?volume_percent=" + volume))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public int getVolume() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spotify.com/v1/me/player"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                JSONObject json = new JSONObject(body);
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
