package com.yakovliam.spaceapi.text;

import com.google.common.base.Joiner;
import com.yakovliam.spaceapi.config.adapter.ConfigurationAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class Message {

    private static final char DELIMITER = '$';

    private List<String> lines;
    private List<Extra> extras;

    public static Message fromConfigurationSection(ConfigurationAdapter adapter, String key) {
        List<String> text = adapter.getStringList(key + ".text", Collections.emptyList());
        if (text.isEmpty()) text = Collections.singletonList(adapter.getString(key + ".text", ""));

        List<Extra> extras = new ArrayList<>(); // todo parse
        return new Message(text, extras);
    }

    public List<BaseComponent[]> toBaseComponents() {

        List<BaseComponent[]> components = new ArrayList<>();

        int count = 0;
        for (String text : lines) {
            text = ChatColor.translateAlternateColorCodes('&', text);

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
                        if (extra.click != null) {
                            evt.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, extra.click));
                        }
                        if (extra.suggest != null) {
                            evt.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, extra.suggest));
                        }
                        // todo add the rest

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
    @Setter
    public static class Extra {
        private List<String> tooltip;
        private String click;
        private String suggest;
    }
}
