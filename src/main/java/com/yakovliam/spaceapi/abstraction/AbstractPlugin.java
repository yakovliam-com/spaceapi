package com.yakovliam.spaceapi.abstraction;

import com.yakovliam.spaceapi.command.Command;

public abstract class AbstractPlugin {

    public abstract void registerCommand(Command command);

}
