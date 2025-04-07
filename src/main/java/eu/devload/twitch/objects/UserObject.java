package eu.devload.twitch.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(fluent = true)
public class UserObject {

    private String id;
    private String login;
    private String displayName;
    private String broadcasterType;
    private String description;
    private String profileImageUrl;
    private long createdAt;

}
