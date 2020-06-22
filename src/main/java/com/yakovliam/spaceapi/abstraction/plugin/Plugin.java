package com.yakovliam.spaceapi.abstraction.plugin;

import com.yakovliam.spaceapi.command.Command;

import java.io.File;

public abstract class Plugin {

    public abstract void registerCommand(Command command);

    public abstract File getDataFolder();

}
