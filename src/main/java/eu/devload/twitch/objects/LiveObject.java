package eu.devload.twitch.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class LiveObject {

    private String channelId;
    private String title;
    private String game;
    private int viewer;

}
