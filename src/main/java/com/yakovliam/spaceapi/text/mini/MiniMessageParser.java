package com.yakovliam.spaceapi.text.mini;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yakovliam.spaceapi.text.mini.MiniMessageParser.Constants.*;

public class MiniMessageParser {

    // regex group names
    private static final String START = "start";
    private static final String TOKEN = "token";
    private static final String INNER = "inner";
    private static final String END = "end";
    // https://regex101.com/r/8VZ7uA/5
    private static final Pattern pattern = Pattern.compile("((?<start><)(?<token>([^<>]+)|([^<>]+\"(?<inner>[^\"]+)\"))(?<end>>))+?");

    public static String escapeTokens(String richMessage) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(richMessage);
        int lastEnd = 0;
        while (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            if (startIndex > lastEnd) {
                sb.append(richMessage, lastEnd, startIndex);
            }
            lastEnd = endIndex;

            String start = matcher.group(START);
            String token = matcher.group(TOKEN);
            String inner = matcher.group(INNER);
            String end = matcher.group(END);

            // also escape inner
            if (inner != null) {
                token = token.replace(inner, escapeTokens(inner));
            }

            sb.append("\\").append(start).append(token).append("\\").append(end);
        }

        if (richMessage.length() > lastEnd) {
            sb.append(richMessage.substring(lastEnd));
        }

        return sb.toString();
    }

    public static String stripTokens(String richMessage) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(richMessage);
        int lastEnd = 0;
        while (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            if (startIndex > lastEnd) {
                sb.append(richMessage, lastEnd, startIndex);
            }
            lastEnd = endIndex;
        }

        if (richMessage.length() > lastEnd) {
            sb.append(richMessage.substring(lastEnd));
        }

        return sb.toString();
    }


    public static String handlePlaceholders(String richMessage, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new ParseException(
                    "Invalid number placeholders defined, usage: parseFormat(format, key, value, key, value...)");
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            richMessage = richMessage.replace(TAG_START + placeholders[i] + TAG_END, placeholders[i + 1]);
        }
        return richMessage;
    }


    public static String handlePlaceholders(String richMessage, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            richMessage = richMessage.replace(TAG_START + entry.getKey() + TAG_END, entry.getValue());
        }
        return richMessage;
    }


    public static BaseComponent[] parseFormat(String richMessage, String... placeholders) {
        return parseFormat(handlePlaceholders(richMessage, placeholders));
    }


    public static BaseComponent[] parseFormat(String richMessage, Map<String, String> placeholders) {
        return parseFormat(handlePlaceholders(richMessage, placeholders));
    }


    public static BaseComponent[] parseFormat(String richMessage) {
        ComponentBuilder builder = null;

        Deque<ClickEvent> clickEvents = new ArrayDeque<>();
        Deque<HoverEvent> hoverEvents = new ArrayDeque<>();
        Deque<ChatColor> colors = new ArrayDeque<>();
        EnumSet<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);

        Matcher matcher = pattern.matcher(richMessage);
        int lastEnd = 0;
        while (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            String msg = null;
            if (startIndex > lastEnd) {
                msg = richMessage.substring(lastEnd, startIndex);
            }
            lastEnd = endIndex;

            // handle message
            if (msg != null && msg.length() != 0) {
                // append message
                if (builder == null) {
                    builder = new ComponentBuilder(msg);
                } else {
                    builder.append(msg, ComponentBuilder.FormatRetention.NONE);
                }

                // set everything that is not closed yet
                if (!clickEvents.isEmpty()) {
                    builder.event(clickEvents.peek());
                }
                if (!hoverEvents.isEmpty()) {
                    builder.event(hoverEvents.peek());
                }
                if (!colors.isEmpty()) {
                    builder.color(colors.peek());
                }
                if (!decorations.isEmpty()) {
                    // no lambda because builder isn't effective final :/
                    for (TextDecoration decor : decorations) {
                        decor.apply(builder);
                    }
                }
            }

            String token = matcher.group(TOKEN);
            String inner = matcher.group(INNER);

            Optional<TextDecoration> deco;
            Optional<ChatColor> color;

            // click
            if (token.startsWith(CLICK + SEPARATOR)) {
                clickEvents.push(handleClick(token, inner));
            } else if (token.equals(CLOSE_TAG + CLICK)) {
                clickEvents.pop();
            }
            // hover
            else if (token.startsWith(HOVER + SEPARATOR)) {
                hoverEvents.push(handleHover(token, inner));
            } else if (token.equals(CLOSE_TAG + HOVER)) {
                hoverEvents.pop();
            }
            // decoration
            else if ((deco = resolveDecoration(token)).isPresent()) {
                decorations.add(deco.get());
            } else if (token.startsWith(CLOSE_TAG) && (deco = resolveDecoration(token.replace(CLOSE_TAG, ""))).isPresent()) {
                decorations.remove(deco.get());
            }
            // color
            else if ((color = resolveColor(token)).isPresent()) {
                colors.push(color.get());
            } else if (token.startsWith(CLOSE_TAG) && resolveColor(token.replace(CLOSE_TAG, "")).isPresent()) {
                colors.pop();
            } else {
                // invalid tag
                if (builder == null) {
                    builder = new ComponentBuilder(TAG_START + token + TAG_END);
                } else {
                    builder.append(TAG_START + token + TAG_END, ComponentBuilder.FormatRetention.NONE);
                }
            }
        }

        // handle last message part
        if (richMessage.length() > lastEnd) {
            String msg = richMessage.substring(lastEnd);
            // append message
            if (builder == null) {
                builder = new ComponentBuilder(msg);
            } else {
                builder.append(msg, ComponentBuilder.FormatRetention.NONE);
            }

            // set everything that is not closed yet
            if (!clickEvents.isEmpty()) {
                builder.event(clickEvents.peek());
            }
            if (!hoverEvents.isEmpty()) {
                builder.event(hoverEvents.peek());
            }
            if (!colors.isEmpty()) {
                builder.color(colors.peek());
            }
            if (!decorations.isEmpty()) {
                // no lambda because builder isn't effective final :/
                for (TextDecoration decor : decorations) {
                    decor.apply(builder);
                }
            }
        }

        if (builder == null) {
            // lets just return an empty component
            builder = new ComponentBuilder("");
        }

        return builder.create();
    }


    private static ClickEvent handleClick(String token, String inner) {
        String[] args = token.split(SEPARATOR);
        if (args.length < 2) {
            throw new ParseException("Can't parse click action (too few args) " + token);
        }
        ClickEvent.Action action = ClickEvent.Action.valueOf(args[1].toUpperCase(Locale.ROOT));
        return new ClickEvent(action, token.replace(CLICK + SEPARATOR + args[1] + SEPARATOR, ""));
    }


    private static HoverEvent handleHover(String token, String inner) {
        String[] args = token.split(SEPARATOR);
        if (args.length < 2) {
            throw new ParseException("Can't parse hover action (too few args) " + token);
        }
        HoverEvent.Action action = HoverEvent.Action.valueOf(args[1].toUpperCase(Locale.ROOT));
        return new HoverEvent(action, parseFormat(inner));
    }


    private static Optional<ChatColor> resolveColor(String token) {
        try {
            return Optional.of(ChatColor.valueOf(token.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();

        }
    }


    private static Optional<TextDecoration> resolveDecoration(String token) {
        try {
            return Optional.of(TextDecoration.valueOf(token.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    enum TextDecoration {
        BOLD(b -> b.bold(true)),
        ITALIC(b -> b.italic(true)),
        UNDERLINED(b -> b.underlined(true)),
        STRIKETHROUGH(b -> b.strikethrough(true)),
        OBFUSCATED(b -> b.obfuscated(true));

        private final Consumer<ComponentBuilder> builder;

        TextDecoration(Consumer<ComponentBuilder> builder) {
            this.builder = builder;
        }

        public void apply(ComponentBuilder comp) {
            builder.accept(comp);
        }
    }

    static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }

    class Constants {

        public static final String CLICK = "click";
        public static final String HOVER = "hover";

        public static final String UNDERLINED = "underlined";
        public static final String STRIKETHROUGH = "strikethrough";
        public static final String OBFUSCATED = "obfuscated";
        public static final String ITALIC = "italic";
        public static final String BOLD = "bold";

        public static final String TAG_START = "<";
        public static final String TAG_END = ">";
        public static final String CLOSE_TAG = "/";
        public static final String SEPARATOR = ":";

        private Constants() {
        }
    }
}