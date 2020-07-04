package dev.spaceseries.api.abstraction.server;

import dev.spaceseries.api.command.BukkitSpaceCommandSender;
import dev.spaceseries.api.command.SpaceCommandSender;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class BukkitServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BukkitSpaceCommandSender::new);
    }
}
