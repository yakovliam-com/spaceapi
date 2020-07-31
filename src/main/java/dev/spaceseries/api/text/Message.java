package dev.spaceseries.api.text;

import com.google.common.base.Joiner;
import dev.spaceseries.api.abstraction.server.Server;
import dev.spaceseries.api.command.SpaceCommandSender;
import dev.spaceseries.api.config.impl.Configuration;
import dev.spaceseries.api.util.ColorUtil;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private final List<String> richLines;

    private Message(String ident, List<String> lines, List<Extra> extras, List<String> richLines) {
        this.ident = ident;
        this.lines = lines;
        this.extras = extras;
        this.richLines = richLines;
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

    public static Builder fromConfigurationSection(Configuration section, String ident) {
        // create new builder
        Builder builder = Message.builder(ident);

        // if rich exists, return builder as-is but with rich
        List<String> rich = section.getStringList("rich", Collections.emptyList());
        if (rich != null && !rich.isEmpty()) {
            // return builder with rich text
            return builder.setRichLines(rich);
        }

        // initialize empty list
        List<String> text = Collections.emptyList();

        // get text lines
        text = section.getStringList("text", text);

        // set text in builder
        text.forEach(builder::addLine);

        // get extras section
        Configuration extrasSection = section.getSection("extras");
        // get list of keys
        Collection<String> extraKeys = extrasSection.getKeys();

        // if no keys, return builder as-is
        if (extraKeys.size() <= 0) return builder;

        // initialize new extras list
        List<Extra> extras = new ArrayList<>();

        // loop through keys and create extras
        extraKeys.forEach(key -> {
            // get section from key
            Configuration keySection = extrasSection.getSection(key);

            // get action
            String action = keySection.getString("action", null);

            // get content
            String content = keySection.getString("content", null);

            // (if applicable) get tooltip
            List<String> tooltip = keySection.getStringList("tooltip", Collections.emptyList());

            // create new extra
            Extra extra = new Extra();

            // if there's an action, add it
            if (action != null && content != null) {
                Extra.ClickAction actionType;
                try {
                    actionType = Extra.ClickAction.valueOf(action.toUpperCase());

                    // set action
                    extra.withAction(actionType, content);
                } catch (Exception ignored) {
                }
            }

            // add tooltip (if not empty)
            if (!tooltip.isEmpty()) extra.withTooltip(tooltip);

            // add extra to extras list
            extras.add(extra);
        });

        // add extras to builder
        extras.forEach(builder::addExtra);

        // return builder
        return builder;
    }

    public void broadcast(String... replacers) {
        msg(Server.get().getOnlinePlayers().collect(Collectors.toList()), replacers);
    }

    public static Builder builder(String ident) {
        return new Message.Builder(ident);
    }

    public List<BaseComponent[]> toBaseComponents(String... replacers) {
        List<BaseComponent[]> components = new ArrayList<>();

//        // if rich text is present
//        if (richLines != null && !richLines.isEmpty()) {
//            // add the parsed lines to the components list
//            components.addAll(richLines.stream().map(line -> MiniMessage.get().parse(line, replacers)).collect(Collectors.toList()));
//            // return as-is
//            return components;
//        }

        int count = 0;
        for (String text : lines) {
            text = ColorUtil.translateFromAmpersand(text);

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
                    cb.append(ColorUtil.fromLegacyText(text.substring(i2 + 1, i1)));

                    i2 = text.indexOf(DELIMITER, i1 + 1);
                    if (i2 != -1) {
                        BaseComponent[] extras = ColorUtil.fromLegacyText(text.substring(i1 + 1, i2));
                        ComponentBuilder evt = new ComponentBuilder("").append(new TextComponent(extras));

                        Extra extra;

                        if (this.extras.size() <= count) {
                            extra = new Extra();
                        } else {
                            extra = this.extras.get(count++);
                        }

                        if (extra.tooltip != null) {
                            evt.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    ColorUtil.fromLegacyText(Joiner.on("\n").join(extra.tooltip.stream()
                                            .map(ColorUtil::translateFromAmpersand)
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

            if (i1 == -1) {
                cb.append(ColorUtil.fromLegacyText(text.substring(i2 + 1)));
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

        private final String ident;

        private final List<String> lines = new ArrayList<>();
        private final List<Extra> extras = new ArrayList<>();
        private List<String> richLines;

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

        public Builder setRichLines(List<String> richLines) {
            this.richLines = richLines;
            return this;
        }

        public Message build() {
            return new Message(ident, lines, extras, richLines);
        }
    }
}
