package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.Codec;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StyleSerializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();

    static final String FONT = "font";
    static final String COLOR = "color";
    static final String INSERTION = "insertion";
    static final String CLICK_EVENT = "clickEvent";
    static final String CLICK_EVENT_ACTION = "action";
    static final String CLICK_EVENT_VALUE = "value";
    static final String HOVER_EVENT = "hoverEvent";
    static final String HOVER_EVENT_ACTION = "action";
    static final String HOVER_EVENT_CONTENTS = "contents";
    static final @Deprecated
    String HOVER_EVENT_VALUE = "value";

    private final LegacyHoverEventSerializer legacyHover;
    private final boolean emitLegacyHover;

    /* package */ StyleSerializer(final @Nullable LegacyHoverEventSerializer legacyHover, final boolean emitLegacyHover) {
        this.legacyHover = legacyHover;
        this.emitLegacyHover = emitLegacyHover;
    }

    @Override
    public Style deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        return this.deserialize(object, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Style deserialize(final JsonObject json, final JsonDeserializationContext context) throws JsonParseException {
        final Style.Builder style = Style.builder();

        if (json.has(FONT)) {
            style.font(context.deserialize(json.get(FONT), Key.class));
        }

        if (json.has(COLOR)) {
            final TextColorWrapper color = context.deserialize(json.get(COLOR), TextColorWrapper.class);
            if (color.color != null) {
                style.color(color.color);
            } else if (color.decoration != null) {
                // I know. Setting a decoration from the color is weird. This is, unfortunately, something we need to support.
                style.decoration(color.decoration, true);
            }
        }

        for (int i = 0, length = DECORATIONS.length; i < length; i++) {
            final TextDecoration decoration = DECORATIONS[i];
            final String name = TextDecoration.NAMES.key(decoration);
            if (json.has(name)) {
                style.decoration(decoration, json.get(name).getAsBoolean());
            }
        }

        if (json.has(INSERTION)) {
            style.insertion(json.get(INSERTION).getAsString());
        }

        if (json.has(CLICK_EVENT)) {
            final JsonObject clickEvent = json.getAsJsonObject(CLICK_EVENT);
            if (clickEvent != null) {
                final /* @Nullable */ JsonPrimitive rawAction = clickEvent.getAsJsonPrimitive(CLICK_EVENT_ACTION);
                final ClickEvent./*@Nullable*/ Action action = rawAction == null ? null : context.deserialize(rawAction, ClickEvent.Action.class);
                if (action != null && action.readable()) {
                    final /* @Nullable */ JsonPrimitive rawValue = clickEvent.getAsJsonPrimitive(CLICK_EVENT_VALUE);
                    final /* @Nullable */ String value = rawValue == null ? null : rawValue.getAsString();
                    if (value != null) {
                        style.clickEvent(ClickEvent.of(action, value));
                    }
                }
            }
        }

        if (json.has(HOVER_EVENT)) {
            final JsonObject hoverEvent = json.getAsJsonObject(HOVER_EVENT);
            if (hoverEvent != null) {
                final /* @Nullable */ JsonPrimitive rawAction = hoverEvent.getAsJsonPrimitive(HOVER_EVENT_ACTION);
                final HoverEvent./*@Nullable*/ Action action = rawAction == null ? null : context.deserialize(rawAction, HoverEvent.Action.class);
                if (action != null && action.readable()) {
                    final /* @Nullable */ Object value;
                    if (hoverEvent.has(HOVER_EVENT_CONTENTS)) {
                        final /* @Nullable */ JsonElement rawValue = hoverEvent.get(HOVER_EVENT_CONTENTS);
                        value = context.deserialize(rawValue, action.type());
                    } else if (hoverEvent.has(HOVER_EVENT_VALUE)) {
                        final Component rawValue = context.deserialize(hoverEvent.get(HOVER_EVENT_VALUE), Component.class);
                        value = this.legacyHoverEventContents(action, rawValue, context);
                    } else {
                        value = null;
                    }

                    if (value != null) {
                        style.hoverEvent(HoverEvent.of(action, value));
                    }
                }
            }
        }

        if (json.has(FONT)) {
            style.font(context.deserialize(json.get(FONT), Key.class));
        }

        return style.build();
    }

    private Object legacyHoverEventContents(final HoverEvent.Action<?> action, final Component rawValue, final JsonDeserializationContext context) {
        if (action == HoverEvent.Action.SHOW_TEXT) { // Passthrough -- no serialization needed
            return rawValue;
        } else if (this.legacyHover != null) {
            try {
                if (action == HoverEvent.Action.SHOW_ENTITY) {
                    return this.legacyHover.deserializeShowEntity(rawValue, this.decoder(context));
                } else if (action == HoverEvent.Action.SHOW_ITEM) {
                    return this.legacyHover.deserializeShowItem(rawValue);
                }
            } catch (final IOException ex) {
                throw new JsonParseException(ex);
            }
        }
        // if we can't handle
        throw new UnsupportedOperationException();
    }

    private Codec.Decoder<Component, String, JsonParseException> decoder(final JsonDeserializationContext ctx) {
        return str -> {
            final JsonReader reader = new JsonReader(new StringReader(str));
            return ctx.deserialize(Streams.parse(reader), Component.class);
        };
    }

    @Override
    public JsonElement serialize(final Style src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject json = new JsonObject();

        final /* @Nullable */ Key font = src.font();
        if (font != null) {
            json.add(FONT, context.serialize(font));
        }

        final /* @Nullable */ TextColor color = src.color();
        if (color != null) {
            json.add(COLOR, context.serialize(color));
        }

        for (int i = 0, length = DECORATIONS.length; i < length; i++) {
            final TextDecoration decoration = DECORATIONS[i];
            final TextDecoration.State state = src.decoration(decoration);
            if (state != TextDecoration.State.NOT_SET) {
                final String name = TextDecoration.NAMES.key(decoration);
                json.addProperty(name, state == TextDecoration.State.TRUE);
            }
        }

        final /* @Nullable */ String insertion = src.insertion();
        if (insertion != null) {
            json.addProperty(INSERTION, insertion);
        }

        final /* @Nullable */ ClickEvent clickEvent = src.clickEvent();
        if (clickEvent != null) {
            final JsonObject eventJson = new JsonObject();
            eventJson.add(CLICK_EVENT_ACTION, context.serialize(clickEvent.action()));
            eventJson.addProperty(CLICK_EVENT_VALUE, clickEvent.value());
            json.add(CLICK_EVENT, eventJson);
        }

        final /* @Nullable */ HoverEvent<?> hoverEvent = src.hoverEvent();
        if (hoverEvent != null) {
            final JsonObject eventJson = new JsonObject();
            eventJson.add(HOVER_EVENT_ACTION, context.serialize(hoverEvent.action()));
            final JsonElement modernContents = context.serialize(hoverEvent.value());
            eventJson.add(HOVER_EVENT_CONTENTS, modernContents);
            if (this.emitLegacyHover) {
                eventJson.add(HOVER_EVENT_VALUE, this.serializeLegacyHoverEvent(hoverEvent, modernContents, context));
            }
            json.add(HOVER_EVENT, eventJson);
        }

        return json;
    }

    private JsonElement serializeLegacyHoverEvent(final HoverEvent<?> hoverEvent, final JsonElement modernContents, final JsonSerializationContext context) {
        if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) { // serialization is the same
            return modernContents;
        } else if (this.legacyHover != null) { // for data formats that require knowledge of SNBT
            Component serialized = null;
            try {
                if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
                    serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity) hoverEvent.value(), this.encoder(context));
                } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
                    serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem) hoverEvent.value());
                }
            } catch (final IOException ex) {
                throw new JsonSyntaxException(ex);
            }
            return serialized == null ? JsonNull.INSTANCE : context.serialize(serialized);
        } else {
            return JsonNull.INSTANCE;
        }
    }

    private Codec.Encoder<Component, String, RuntimeException> encoder(final JsonSerializationContext ctx) {
        return component -> ctx.serialize(component).toString();
    }
}
