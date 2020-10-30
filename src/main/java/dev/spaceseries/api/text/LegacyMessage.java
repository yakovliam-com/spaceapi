package dev.spaceseries.api.text;

import dev.spaceseries.api.command.SpaceCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyMessage {

    private static ChatColor main;
    private static ChatColor info;
    private static ChatColor success;
    private static ChatColor error;
    private static ChatColor highlight;
    private static ChatColor lowKey;

    public static void setMain(ChatColor main) {
        LegacyMessage.main = main;
    }

    public static void setSuccess(ChatColor success) {
        LegacyMessage.success = success;
    }

    public static void setError(ChatColor error) {
        LegacyMessage.error = error;
    }

    public static void setHighlight(ChatColor highlight) {
        LegacyMessage.highlight = highlight;
    }

    public static void setLowKey(ChatColor lowKey) {
        LegacyMessage.lowKey = lowKey;
    }

    public static void setInfo(ChatColor info) {
        LegacyMessage.info = info;
    }

    private final String ident;

    private final List<BaseComponent[]> componentsLines;

    private static final Map<String, LegacyMessage> messageMap = new HashMap<>();

    private LegacyMessage(String ident, List<BaseComponent[]> components) {
        this.ident = ident;
        this.componentsLines = components;

        messageMap.put(ident.toLowerCase(), this);
    }

    public void msg(SpaceCommandSender sender, String... replacers) {
        msg(Collections.singletonList(sender), replacers);
    }

    public void msg(Collection<SpaceCommandSender> senders, String... replacers) {
        for (BaseComponent[] components : componentsLines) {
            List<BaseComponent> componentList = new ArrayList<>();
            for (BaseComponent component : components) {
                BaseComponent duplicate = component.duplicate();
                componentList.add(duplicate);

                if (duplicate instanceof TextComponent) {
                    String message = ((TextComponent) duplicate).getText();

                    String left = null;
                    for (String right : replacers) {
                        if (left == null)
                            left = right;
                        else {
                            message = message.replaceAll(Pattern.quote(left), Matcher.quoteReplacement(right));
                            left = null;
                        }
                    }

                    ((TextComponent) duplicate).setText(message);
                }
            }

            for (SpaceCommandSender sender : senders) {
                BaseComponent[] c = componentList.toArray(new BaseComponent[]{});

                if (!sender.isPlayer()) {
                    sender.sendMessage(TextComponent.toLegacyText(c));
                } else {
                    sender.sendMessage(c);
                }
            }
        }
    }

    public List<String> get(SpaceCommandSender sender, String... replacers) {
        FakeCommandSender fakeSender = new FakeCommandSender(sender);
        msg(fakeSender, replacers);
        return fakeSender.getMessages();
    }

    //public void broadcast(String... replacers) {
    //    msg(new ArrayList<>(Bukkit.getOnlinePlayers()), replacers);
    //} // todo make it cross compatible

    public static Builder builder(String ident) {
        return new Builder(ident);
    }

    public static Builder builder(String ident, boolean translateColorCodes) {
        return new Builder(ident, translateColorCodes);
    }

    public static class Builder {

        private final String ident;
        private final Boolean translateColorCodes;
        private final List<ComponentBuilder> cb = new ArrayList<>();

        private Builder(String ident) {
            this.ident = ident;
            this.translateColorCodes = false;
            cb.add(new ComponentBuilder("").retain(ComponentBuilder.FormatRetention.NONE));
        }

        private Builder(String ident, boolean translateColorCodes) {
            this.ident = ident;
            this.translateColorCodes = translateColorCodes;
            cb.add(new ComponentBuilder("").retain(ComponentBuilder.FormatRetention.NONE));
        }

        public Builder main(String text) {
            return main(text, false);
        }

        public Builder main(String text, boolean bold) {
            getComponentBuilder().append(legacyColor(text)).color(main).bold(bold);
            return this;
        }

        public Builder info(String text) {
            return info(legacyColor(text), false);
        }

        public Builder info(String text, boolean bold) {
            getComponentBuilder().append(legacyColor(text)).color(info).bold(bold);
            return this;
        }

        public Builder success(String text) {
            return success(text, false);
        }

        public Builder success(String text, boolean bold) {
            getComponentBuilder().append(legacyColor(text)).color(success).bold(bold);
            return this;
        }

        public Builder error(String text) {
            return error(text, false);
        }

        public Builder error(String text, boolean bold) {
            getComponentBuilder().append(legacyColor(text)).color(error).bold(bold);
            return this;
        }

        public Builder highlight(String text) {
            return highlight(text, false);
        }

        public Builder highlight(String text, boolean bold) {
            getComponentBuilder().append(legacyColor(text)).color(highlight).bold(bold);
            return this;
        }

        public Builder newLine() {
            cb.add(new ComponentBuilder("").retain(ComponentBuilder.FormatRetention.NONE));
            return this;
        }

        private String legacyColor(String s) {
            if (!translateColorCodes) return s;
            return ChatColor.translateAlternateColorCodes('&', s);
        }

        public LegacyMessage build() {
            List<BaseComponent[]> list = new ArrayList<>();
            for (ComponentBuilder componentBuilder : cb) {
                list.add(componentBuilder.create());
            }
            return new LegacyMessage(ident, list);
        }

        public ComponentBuilder getComponentBuilder() {
            return cb.get(cb.size() - 1);
        }
    }

    public static class Global {
        public static final LegacyMessage ACCESS_DENIED = LegacyMessage.builder("access-denied")
                .error(ChatColor.RED + "Insufficient permissions!")
                .build();

        public static final LegacyMessage PLAYERS_ONLY = LegacyMessage.builder("players-only")
                .error(ChatColor.RED + "Only players are allowed to do this.")
                .build();

        public static final LegacyMessage PLAYER_NOT_FOUND = LegacyMessage.builder("player-not-found")
                .error(ChatColor.RED + "Player not found!")
                .build();

    }
}
