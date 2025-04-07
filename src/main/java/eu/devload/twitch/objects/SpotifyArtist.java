package eu.devload.twitch.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class SpotifyArtist {

    private String id;
    private String name;
    private String href;
    private String spotifyUrl;
    private String uri;

}
