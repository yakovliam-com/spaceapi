package com.yakovliam.spaceapi.text;

import com.yakovliam.spaceapi.command.SpaceCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private static ChatColor main;
    private static ChatColor info;
    private static ChatColor success;
    private static ChatColor error;
    private static ChatColor highlight;
    private static ChatColor lowKey;

    public static void setMain(ChatColor main) {
        Message.main = main;
    }

    public static void setSuccess(ChatColor success) {
        Message.success = success;
    }

    public static void setError(ChatColor error) {
        Message.error = error;
    }

    public static void setHighlight(ChatColor highlight) {
        Message.highlight = highlight;
    }

    public static void setLowKey(ChatColor lowKey) {
        Message.lowKey = lowKey;
    }

    public static void setInfo(ChatColor info) {
        Message.info = info;
    }

    private final String ident;

    private final List<BaseComponent[]> componentsLines;

    private static Map<String, Message> messageMap = new HashMap<>();

    private Message(String ident, List<BaseComponent[]> components) {
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
        private List<ComponentBuilder> cb = new ArrayList<>();

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

        public Message build() {
            List<BaseComponent[]> list = new ArrayList<>();
            for (ComponentBuilder componentBuilder : cb) {
                list.add(componentBuilder.create());
            }
            return new Message(ident, list);
        }

        public ComponentBuilder getComponentBuilder() {
            return cb.get(cb.size() - 1);
        }
    }

    public static class Global {
        public static final Message ACCESS_DENIED = Message.builder("access-denied")
                .error("Insufficient permissions!")
                .build();

        public static final Message PLAYERS_ONLY = Message.builder("players-only")
                .error("Only players are allowed to do this.")
                .build();

        public static final Message PLAYER_NOT_FOUND = Message.builder("player-not-found")
                .error("Player not found!")
                .build();

    }
}
