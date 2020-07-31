package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.Nullable;

/*
 * This is a hack.
 */
final class TextColorWrapper {
    final @Nullable TextColor color;
    final @Nullable TextDecoration decoration;
    final boolean reset;

    /* package */ TextColorWrapper(final @Nullable TextColor color, final @Nullable TextDecoration decoration, final boolean reset) {
        this.color = color;
        this.decoration = decoration;
        this.reset = reset;
    }

    /* package */ static class Serializer extends TypeAdapter<TextColorWrapper> {
        @Override
        public void write(final JsonWriter out, final TextColorWrapper value) {
            throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
        }

        @Override
        public TextColorWrapper read(final JsonReader in) throws IOException {
            final String input = in.nextString();
            final TextColor color = TextColorSerializer.fromString(input);
            final TextDecoration decoration = TextDecoration.NAMES.value(input);
            final boolean reset = decoration == null && input.equals("reset");
            if (color == null && decoration == null && !reset) {
                throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
            }
            return new TextColorWrapper(color, decoration, reset);
        }
    }
}
