package com.yakovliam.spaceapi.abstraction.server;

import com.yakovliam.spaceapi.command.SpaceCommandSender;

import java.util.stream.Stream;

public abstract class Server {

    private static Server instance;

    public static Server get() {
        if (instance == null) {
            try {
                Class.forName("org.bukkit.Bukkit");
                instance = new BukkitServer();
            } catch (ClassNotFoundException e) {
                instance = new BungeeServer();
            }
        }
        return instance;
    }

    public abstract Stream<SpaceCommandSender> getOnlinePlayers();
}
