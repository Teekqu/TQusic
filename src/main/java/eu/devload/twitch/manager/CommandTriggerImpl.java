package eu.devload.twitch.manager;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandTriggerImpl {

    private static List<Class<?>> commands = new ArrayList<>();

    public static void addCommand(Class<?> clazz) {
        if(!commands.contains(clazz)) commands.add(clazz);
    }

    public static void removeCommand(Class<?> clazz) {
        commands.remove(clazz);
    }

    public static void triggerCommand(TwitchChannel channel, EventUser user, String command, String[] args) {
        for (Class<?> cmd : commands) {
            try {
                Method method = Arrays.stream(cmd.getMethods()).filter(m -> m.getName().equalsIgnoreCase("execute")).findFirst().orElse(null);
                if (method == null) return;
                method.invoke(cmd.getDeclaredConstructor().newInstance(), channel, user, command, args);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public static void startEventCheck() {
        SystemAPI.get().eventHandler().onEvent(ChannelMessageEvent.class, e -> {
            if(!Objects.equals(e.getChannel().getId(), e.getSourceChannelId().orElse(e.getChannel().getId()))) return;
            if (!e.getMessage().startsWith("!")) return;
            if (e.getChannel().getId().equals(ClientUser.get().id())) return;
            String[] splits = e.getMessage().split(" ");
            String command = splits[0].substring(1);
            String[] args = new String[splits.length - 1];
            System.arraycopy(splits, 1, args, 0, args.length);
            TwitchChannel channel = CacheManager.get().getChannel(e.getChannel().getId());
            triggerCommand(channel, e.getUser(), command, args);
        });
    }

}
