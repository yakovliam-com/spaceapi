package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

final class ShowEntitySerializer implements JsonDeserializer<HoverEvent.ShowEntity>, JsonSerializer<HoverEvent.ShowEntity> {
    static final String TYPE = "type";
    static final String ID = "id";
    static final String NAME = "name";

    @Override
    public HoverEvent.ShowEntity deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        if (!object.has(TYPE) || !object.has(ID)) {
            throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
        }

        final Key type = context.deserialize(object.getAsJsonPrimitive(TYPE), Key.class);
        final UUID id = UUID.fromString(object.getAsJsonPrimitive(ID).getAsString());

        /* @Nullable */
        Component name = null;
        if (object.has(NAME)) {
            name = context.deserialize(object.get(NAME), Component.class);
        }

        return new HoverEvent.ShowEntity(type, id, name);
    }

    @Override
    public JsonElement serialize(final HoverEvent.ShowEntity src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject json = new JsonObject();

        json.add(TYPE, context.serialize(src.type()));
        json.addProperty(ID, src.id().toString());

        final /* @Nullable */ Component name = src.name();
        if (name != null) {
            json.add(NAME, context.serialize(name));
        }

        return json;
    }
}
