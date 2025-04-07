package eu.devload.twitch.utils;

import com.github.twitch4j.helix.domain.UserList;

import java.util.Collections;

public class Convert {

    public static String secondsToFormat(long seconds) {
        if(seconds == 0L) return "0 seconds";

        int[] times = new int[]{(int)(seconds / 31536000L), (int)(seconds % 31536000L / 2592000L), (int)(seconds % 31536000L % 2592000L / 86400L), (int)(seconds % 31536000L % 2592000L % 86400L / 3600L), (int)(seconds % 31536000L % 2592000L % 86400L % 3600L / 60L)};
        boolean listMinutes = times[0] == 0 && times[1] == 0 && times[2] == 0 && times[4] > 0;
        StringBuilder sb = new StringBuilder();

        if (times[0] != 0) sb.append(times[0]).append(" ").append(times[0] > 1 ? "years " : "year ");
        if (times[1] != 0 || times[0] != 0) sb.append(times[1]).append(" ").append(times[1] > 1 ? "months " : "month ");
        if (times[2] != 0 || times[1] != 0 || times[0] != 0) sb.append(times[2]).append(" ").append(times[2] > 1 ? "days " : "day ");
        if (times[3] != 0 || times[2] != 0 || times[1] != 0 || times[0] != 0) sb.append(listMinutes ? "" : "and ").append(times[3]).append(" ").append(times[3] > 1 ? "hours " : "hour ");

        if (listMinutes) sb.append(sb.isEmpty() ? "" : "and ").append(times[4]).append(" ").append(times[4] > 1 ? "minutes " : "minute ");

        return sb.isEmpty() ? "0 seconds" : sb.substring(0, sb.length() - 1);
    }

    public static String channelNameToId(String name) {
        UserList users = SystemAPI.get().client().getHelix().getUsers(null, null, Collections.singletonList(name)).execute();
        return users != null && !users.getUsers().isEmpty() ? (users.getUsers().getFirst()).getId() : null;
    }
}
