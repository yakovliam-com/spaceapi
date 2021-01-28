package dev.spaceseries.api.abstraction.plugin;

import dev.spaceseries.api.command.Command;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

import java.io.File;

public abstract class Plugin {

    public abstract void registerCommand(Command command);

    public abstract File getDataFolder();
}
