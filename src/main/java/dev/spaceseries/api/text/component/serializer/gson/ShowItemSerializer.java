package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;

final class ShowItemSerializer implements JsonDeserializer<HoverEvent.ShowItem>, JsonSerializer<HoverEvent.ShowItem> {
    static final String ID = "id";
    static final String COUNT = "count";
    static final String TAG = "tag";

    ShowItemSerializer() {
    }

    @Override
    public HoverEvent.ShowItem deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        if (!object.has(ID)) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
        }

        final Key id = context.deserialize(object.getAsJsonPrimitive(ID), Key.class);

        int count = 1;
        if (object.has(COUNT)) {
            count = object.get(COUNT).getAsInt();
        }

        BinaryTagHolder nbt = null;
        if (object.has(TAG)) {
            nbt = BinaryTagHolder.of(object.get(TAG).getAsString());
        }

        return new HoverEvent.ShowItem(id, count, nbt);
    }

    @Override
    public JsonElement serialize(final HoverEvent.ShowItem src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject json = new JsonObject();

        json.add(ID, context.serialize(src.item()));

        final int count = src.count();
        if (count != 1) {
            json.addProperty(COUNT, count);
        }

        final /* @Nullable */ BinaryTagHolder nbt = src.nbt();
        if (nbt != null) {
            json.addProperty(TAG, nbt.string());
        }

        return json;
    }
}
