package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextColorSerializer extends TypeAdapter<TextColor> {
    static final TypeAdapter<TextColor> INSTANCE = new TextColorSerializer(false).nullSafe();
    static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = new TextColorSerializer(true).nullSafe();

    private final boolean downsampleColor;

    private TextColorSerializer(final boolean downsampleColor) {
        this.downsampleColor = downsampleColor;
    }

    @Override
    public void write(final JsonWriter out, final TextColor value) throws IOException {
        if (value instanceof NamedTextColor) {
            out.value(NamedTextColor.NAMES.key((NamedTextColor) value));
        } else if (this.downsampleColor) {
            out.value(NamedTextColor.NAMES.key(NamedTextColor.nearestTo(value)));
        } else {
            out.value(value.asHexString());
        }
    }

    @Override
    public TextColor read(final JsonReader in) throws IOException {
        final TextColor color = fromString(in.nextString());
        return this.downsampleColor ? NamedTextColor.nearestTo(color) : color;
    }

    /* package */
    static @Nullable TextColor fromString(final @NonNull String value) {
        if (value.startsWith("#")) {
            return TextColor.fromHexString(value);
        } else {
            return NamedTextColor.NAMES.value(value);
        }
    }
}
