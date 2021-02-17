package dev.spaceseries.spaceapi.abstraction.server;

import dev.spaceseries.spaceapi.command.BungeeSpaceCommandSender;
import dev.spaceseries.spaceapi.command.SpaceCommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.util.stream.Stream;

public class BungeeServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(BungeeSpaceCommandSender::new);
    }
}
