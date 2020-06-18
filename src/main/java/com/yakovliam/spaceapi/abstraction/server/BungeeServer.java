package com.yakovliam.spaceapi.abstraction.server;

import com.yakovliam.spaceapi.command.BungeeSpaceCommandSender;
import com.yakovliam.spaceapi.command.SpaceCommandSender;
import net.md_5.bungee.api.ProxyServer;

import java.util.stream.Stream;

public class BungeeServer extends Server {
    @Override
    public Stream<SpaceCommandSender> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(BungeeSpaceCommandSender::new);
    }
}
