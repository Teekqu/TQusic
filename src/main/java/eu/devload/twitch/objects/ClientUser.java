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

    private final String id = "1210324229";
    private final String displayName = "LumoraBot";
    private final String login = displayName.toLowerCase();


    public String oauth() {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + id + "';");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                return null;
            }
            String accessToken = rs.getString("token");
            String refreshToken = rs.getString("refreshToken");
            long expires = rs.getLong("expiresIn");
            long generated = rs.getLong("generated");
            try { rs.close(); } catch (Exception ignored) { }

            String oauth2;
            if (System.currentTimeMillis() / 1000 > generated + expires) {
                JSONObject json = SystemAPI.get().twitchManager().generateNewOauth2TokenWithRefreshToken(refreshToken);
                if (json == null) {
                    return null;
                }
                String newOauth2 = json.getString("access_token");
                String newRefreshToken = json.getString("refresh_token");
                long expiresIn = json.getLong("expires_in");
                SystemAPI.get().database().execute("UPDATE OauthTokens SET token='" + newOauth2 + "', refreshToken='" + newRefreshToken + "', expiresIn=" + expiresIn + ", generated=" + System.currentTimeMillis() / 1000 + " WHERE id=" + id);
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
