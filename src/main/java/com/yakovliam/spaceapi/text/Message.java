package com.yakovliam.spaceapi.text;

import com.google.common.base.Joiner;
import com.yakovliam.spaceapi.abstraction.server.Server;
import com.yakovliam.spaceapi.command.SpaceCommandSender;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Message {

    private static final char DELIMITER = '$';

    private final String ident;
    private final List<String> lines;
    private final List<Extra> extras;

    private Message(String ident, List<String> lines, List<Extra> extras) {
        this.ident = ident;
        this.lines = lines;
        this.extras = extras;
    }

    public String getIdent() {
        return ident;
    }

    public void msg(SpaceCommandSender sender, String... replacers) {
        msg(Collections.singletonList(sender), replacers);
    }

    public void msg(Iterable<SpaceCommandSender> senders, String... replacers) {
        for (BaseComponent[] c : toBaseComponents(replacers)) {
            for (SpaceCommandSender sender : senders) {
                if (!sender.isPlayer()) {
                    sender.sendMessage(TextComponent.toLegacyText(c));
                } else {
                    sender.sendMessage(c);
                }
            }
        }
    }

    public void broadcast(String... replacers) {
        msg(Server.get().getOnlinePlayers().collect(Collectors.toList()), replacers);
    }

    public static Builder builder(String ident) {
        return new Message.Builder(ident);
    }

    public List<BaseComponent[]> toBaseComponents(String... replacers) {

        List<BaseComponent[]> components = new ArrayList<>();

        int count = 0;
        for (String text : lines) {
            text = ChatColor.translateAlternateColorCodes('&', text);

            String left = null;
            for (String right : replacers) {
                if (left == null)
                    left = right;
                else {
                    text = text.replaceAll(Pattern.quote(left), Matcher.quoteReplacement(right));
                    left = null;
                }
            }

            ComponentBuilder cb = new ComponentBuilder("");

            int i1, i2 = -1;
            do {
                i1 = text.indexOf(DELIMITER, i2 + 1);
                if (i1 != -1) {
                    cb.appendLegacy(text.substring(i2 + 1, i1));

                    i2 = text.indexOf(DELIMITER, i1 + 1);
                    if (i2 != -1) {
                        BaseComponent[] extras = TextComponent.fromLegacyText(text.substring(i1 + 1, i2));
                        ComponentBuilder evt = new ComponentBuilder("").append(new TextComponent(extras));

                        Extra extra = this.extras.get(count++);
                        if (extra.tooltip != null) {
                            evt.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(Joiner.on("\n").join(extra.tooltip.stream()
                                            .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                                            .iterator()))
                            ));
                        }

                        if (extra.action != null) {
                            switch (extra.action) {
                                case RUN_COMMAND:
                                    evt.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, extra.content));
                                    break;
                                case SUGGEST:
                                    evt.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, extra.content));
                                    break;
                                case OPEN_URL:
                                    evt.event(new ClickEvent(ClickEvent.Action.OPEN_URL, extra.content));
                                    break;
                            }
                        }

                        cb.append(evt.create());
                    }

                }
            } while (i1 != -1 && i2 != -1);

            if (i1 == -1 && i2 > 0) {
                cb.appendLegacy(text.substring(i2 + 1));
            }

            components.add(cb.create());
        }

        return components;
    }

    @Getter
    public static class Extra {
        private List<String> tooltip;

        private ClickAction action;
        private String content;

        public Extra() {
        }

        public Extra withAction(ClickAction action, String content) {
            this.action = action;
            this.content = content;
            return this;
        }

        public Extra withTooltip(String... tooltip) {
            this.tooltip = Arrays.asList(tooltip);
            return this;
        }

        public Extra withTooltip(List<String> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public enum ClickAction {
            OPEN_URL,
            RUN_COMMAND,
            SUGGEST
        }
    }

    public static class Builder {

        private String ident;

        private List<String> lines = new ArrayList<>();
        private List<Extra> extras = new ArrayList<>();

        public Builder(String ident) {
            this.ident = ident;
        }

        public Builder addLine(String line) {
            lines.add(line);
            return this;
        }

        public Builder addExtra(Extra extra) {
            extras.add(extra);
            return this;
        }

        public Message build() {
            return new Message(ident, lines, extras);
        }

    }
}
