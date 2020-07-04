package dev.spaceseries.api.abstraction.server;

import dev.spaceseries.api.command.BungeeSpaceCommandSender;
import dev.spaceseries.api.command.SpaceCommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.util.stream.Stream;

public class BungeeServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(BungeeSpaceCommandSender::new);
    }
}
