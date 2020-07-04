package dev.spaceseries.api.command;

public class CommandException extends RuntimeException {

    private final String message;

    public CommandException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
