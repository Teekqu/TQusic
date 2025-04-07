package eu.devload.twitch.api;

import com.sun.net.httpserver.HttpServer;
import eu.devload.twitch.api.admin.JoinChannelHandler;

import java.net.InetSocketAddress;

public class API {

    public static final int PORT = 27908;

    private HttpServer server;

    public API() {

        try {

            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/admin/join", new JoinChannelHandler());

            server.setExecutor(null);
            server.start();
            System.out.println("[API] Started on port " + PORT);

        } catch (Exception err) {
            err.printStackTrace();
        }

    }

}
