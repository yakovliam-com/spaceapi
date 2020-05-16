package com.yakovliam.spaceapi.command;

import com.google.common.base.Joiner;
import com.yakovliam.spaceapi.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.yakovliam.spaceapi.text.Message.Global.ACCESS_DENIED;
import static com.yakovliam.spaceapi.text.Message.Global.PLAYERS_ONLY;

public abstract class Command extends BukkitCommand {

    private Set<Command> subCommands = new HashSet<>();

    public Command(String name) {
        this(name, "No description available.", "N/A", Collections.emptyList());
    }

    public Command(String name, String description) {
        this(name, description, "N/A", Collections.emptyList());
    }

    public Command(String name, String description, List<String> aliases) {
        this(name, description, "N/A", aliases);
    }

    public Command(String name, String description, String usage) {
        this(name, description, usage, Collections.emptyList());
    }

    public Command(String name, String description, String usageMessage, List<String> aliases) {
        super(
                name.toLowerCase(),
                description,
                usageMessage,
                aliases.stream().map(String::toLowerCase).collect(Collectors.toList())
        );

        if (this.getClass().getDeclaredAnnotation(SubCommand.class) == null) register();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player) && this.getClass().getDeclaredAnnotation(PlayersOnly.class) != null) {
            PLAYERS_ONLY.msg(sender);
            return true;
        }

        if (!this.checkPermissions(sender)) {
            ACCESS_DENIED.msg(sender);
            return true;
        }

        if (args.length > 0) {
            for (Command command : subCommands) {
                if (command.getName().equalsIgnoreCase(args[0]) || command.getAliases().contains(args[0].toLowerCase())) {
                    command.execute(sender, commandLabel, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }
        }

        try {
            onCommand(sender, commandLabel, args);
        } catch (CommandException e) {
            sender.sendMessage("Error using " + commandLabel + ": " + e.getMessage());
        }

        return true;
    }

    private boolean checkPermissions(CommandSender sender) {
        Permissible permissible = this.getClass().getDeclaredAnnotation(Permissible.class);
        if (permissible == null) return true;

        for (String s : permissible.value()) {
            if (sender.hasPermission(s)) return true;
        }

        return false;
    }

    public abstract void onCommand(CommandSender sender, String label, String[] args);

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
            return Utility.stringToLongMillisDuration(args[arg]);
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

    private void register() {
        SimpleCommandMap commandMap;

        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        commandMap.register(this.getName(), this);
    }
}
