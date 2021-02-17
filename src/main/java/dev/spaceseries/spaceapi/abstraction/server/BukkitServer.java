package dev.spaceseries.spaceapi.abstraction.server;

import dev.spaceseries.spaceapi.command.BukkitSpaceCommandSender;
import dev.spaceseries.spaceapi.command.SpaceCommandSender;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class BukkitServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BukkitSpaceCommandSender::new);
    }
}
