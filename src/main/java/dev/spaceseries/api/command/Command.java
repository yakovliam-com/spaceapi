package dev.spaceseries.api.command;

import com.google.common.base.Joiner;
import dev.spaceseries.api.abstraction.plugin.Plugin;
import dev.spaceseries.api.util.Utility;
import dev.spaceseries.api.text.LegacyMessage;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Command {

    private Set<Command> subCommands = new HashSet<>();
    private Plugin plugin;
    private String name;
    private String description;
    private String usageMessage;
    private List<String> aliases;

    public Command(Plugin plugin, String name) {
        this(plugin, name, "No description available.", "N/A", Collections.emptyList());
    }

    public Command(Plugin plugin, String name, String description) {
        this(plugin, name, description, "N/A", Collections.emptyList());
    }

    public Command(Plugin plugin, String name, String description, List<String> aliases) {
        this(plugin, name, description, "N/A", aliases);
    }

    public Command(Plugin plugin, String name, String description, String usage) {
        this(plugin, name, description, usage, Collections.emptyList());
    }

    public Command(Plugin plugin, String name, String description, String usageMessage, List<String> aliases) {
        this.plugin = plugin;
        this.name = name.toLowerCase();
        this.description = description;
        this.usageMessage = usageMessage;
        this.aliases = aliases.stream().map(String::toLowerCase).collect(Collectors.toList());

        if (this.getClass().getDeclaredAnnotation(SubCommand.class) == null) plugin.registerCommand(this);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Set<Command> getSubCommands() {
        return subCommands;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public abstract void onCommand(SpaceCommandSender sender, String label, String... args);

    public boolean execute(SpaceCommandSender sender, String commandLabel, String[] args) {
        if (!sender.isPlayer() && this.getClass().getDeclaredAnnotation(PlayersOnly.class) != null) {
            LegacyMessage.Global.PLAYERS_ONLY.msg(sender);
            return true;
        }

        if (!this.checkPermissions(sender)) {
            LegacyMessage.Global.ACCESS_DENIED.msg(sender);
            return true;
        }

        if (args.length > 0) {
            for (Command subCommand : this.getSubCommands()) {
                if (subCommand.getName().equalsIgnoreCase(args[0]) || subCommand.getAliases().contains(args[0].toLowerCase())) {
                    subCommand.execute(sender, commandLabel, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }
        }

        try {
            this.onCommand(sender, commandLabel, args);
        } catch (CommandException e) {
            sender.sendMessage("Error using " + commandLabel + ": " + e.getMessage());
        }

        return true;
    }

    private boolean checkPermissions(SpaceCommandSender sender) {
        Permissible permissible = this.getClass().getDeclaredAnnotation(Permissible.class);
        if (permissible == null) return true;

        for (String s : permissible.value()) {
            if (sender.hasPermission(s)) return true;
        }

        return false;
    }

    public int argAsInt(String[] args, int arg, int defaultVal) {
        try {
            return argAsInt(args, arg);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public int argAsInt(String[] args, int arg) {
        try {
            return Integer.parseInt(args[arg]);
        } catch (Exception e) {
            throw new CommandException("argument " + (arg + 1) + "should be an integer");
        }
    }

    public <T extends Enum<T>> T argAsEnum(String[] args, int arg, Class<T> enom) {
        try {
            T t = Arrays.stream(enom.getEnumConstants()).filter(e -> e.name().equalsIgnoreCase(args[arg])).findAny().orElse(null);
            if (t == null) throw new CommandException("argument " + (arg + 1) + " should be one of: " + Joiner.on(", ").join(enom.getEnumConstants()));

            return t;
        } catch (Exception e) {
            throw new CommandException("argument " + (arg + 1) + " should be one of: " + Joiner.on(", ").join(enom.getEnumConstants()));
        }
    }

    public Long argAsDuration(String[] args, int arg) {
        try {
            return Utility.stringToMillisDuration(args[arg]);
        } catch (Exception e) {
            throw new CommandException("argument " + arg + " should be a duration (e.g. 1h30m, 1d, 5w)");
        }
    }

    public void ensureAtLeast(int num, String[] args) {
        if (args.length < num)
            throw new CommandException("arguments should be at least " + num);
    }

    public void addSubCommands(Command... commands) {
        subCommands.addAll(Arrays.asList(commands));
    }

}
