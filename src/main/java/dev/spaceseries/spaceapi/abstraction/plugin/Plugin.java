package dev.spaceseries.spaceapi.abstraction.plugin;

import dev.spaceseries.spaceapi.command.Command;

import java.io.File;

// TODO Add velocity support
public abstract class Plugin<T> {

    public abstract void registerCommand(Command command);

    public abstract File getDataFolder();

    public abstract T getPlugin();
}
