package com.yakovliam.spaceapi.abstraction.plugin;

import com.yakovliam.spaceapi.command.Command;

public abstract class Plugin {

    public abstract void registerCommand(Command command);

}
