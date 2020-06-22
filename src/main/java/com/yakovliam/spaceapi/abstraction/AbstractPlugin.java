package com.yakovliam.spaceapi.abstraction;

import com.yakovliam.spaceapi.command.Command;

import java.io.File;

public abstract class AbstractPlugin {

    public abstract void registerCommand(Command command);

    public abstract File getDataFolder();

}
