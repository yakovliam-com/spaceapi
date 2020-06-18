package com.yakovliam.spaceapi.abstraction.server;

import com.yakovliam.spaceapi.command.BukkitSpaceCommandSender;
import com.yakovliam.spaceapi.command.SpaceCommandSender;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class BukkitServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BukkitSpaceCommandSender::new);
    }
}
