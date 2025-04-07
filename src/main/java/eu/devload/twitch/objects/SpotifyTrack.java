package eu.devload.twitch.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class SpotifyTrack {

    private String id;
    private String name;
    private String href;
    private String spotifyUrl;
    private String uri;
    private long durationMs;
    private int popularity;
    private boolean isLocal;
    private List<SpotifyArtist> artists;

}
