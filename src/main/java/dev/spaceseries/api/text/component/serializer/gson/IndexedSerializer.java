package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import net.kyori.adventure.util.Index;

final class IndexedSerializer<E> extends TypeAdapter<E> {
    private final String name;
    private final Index<String, E> map;

    public static <E> TypeAdapter<E> of(final String name, final Index<String, E> map) {
        return new IndexedSerializer<>(name, map).nullSafe();
    }

    private IndexedSerializer(final String name, final Index<String, E> map) {
        this.name = name;
        this.map = map;
    }

    @Override
    public void write(final JsonWriter out, final E value) throws IOException {
        out.value(this.map.key(value));
    }

    @Override
    public E read(final JsonReader in) throws IOException {
        final String string = in.nextString();
        final E value = this.map.value(string);
        if (value != null) {
            return value;
        } else {
            throw new JsonParseException("invalid " + this.name + ":  " + string);
        }
    }
}
