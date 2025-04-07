package eu.devload.twitch.objects;

import eu.devload.twitch.utils.SystemAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.sql.ResultSet;

@Getter(value = AccessLevel.PUBLIC)
@Accessors(fluent = true)
public class ClientUser {

    public static ClientUser get() {
        return new ClientUser();
    }

    private final String id = "1294628941";
    private final String displayName = "TQusic";
    private final String login = displayName.toLowerCase();


    public String oauth() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Channels WHERE channelId='" + id + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return null;
            }
            String accessToken = rs.getString("tAccessToken");
            String refreshToken = rs.getString("tRefreshToken");
            long expires = rs.getLong("tExpires");
            try { rs.close(); } catch (Exception ignored) { }

            String oauth2;
            if (System.currentTimeMillis() / 1000 > expires) {
                JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                if (json == null) {
                    return null;
                }
                String newOauth2 = json.getString("access_token");
                String newRefreshToken = json.getString("refresh_token");
                long expiresIn = System.currentTimeMillis() / 1000 + json.getLong("expires_in");
                SystemAPI.get().database().execute("UPDATE Channel SET tAccessToken='" + newOauth2 + "', tRefreshToken='" + newRefreshToken + "', tExpires=" + expiresIn + " WHERE channelId=" + id);
                oauth2 = newOauth2;
                return oauth2;
            }
            oauth2 = accessToken;
            return oauth2;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

}
