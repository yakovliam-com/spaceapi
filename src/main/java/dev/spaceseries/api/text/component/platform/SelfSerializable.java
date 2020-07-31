package dev.spaceseries.api.text.component.platform;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * A representation of a wrapper adapter, generally between and Adventure type and a native type.
 */
interface SelfSerializable {

  /**
   * Write this object to a json writer
   *
   * @param out writer to write to
   * @throws IOException when any serialization-related error occurs
   */
  void write(JsonWriter out) throws IOException;

  /**
   * Gson adapter factory that will appropriately unwrap wrapped values
   */
  class AdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
      if(!SelfSerializable.class.isAssignableFrom(type.getRawType())) {
        return null;
      }

      final TypeAdapter<T> delegated = gson.getDelegateAdapter(this, type);
      return new TypeAdapter<T>() {
        @Override
        public void write(final JsonWriter out, final T value) throws IOException {
          ((SelfSerializable) value).write(out);
        }

        @Override
        public T read(final JsonReader in) throws IOException {
          return delegated.read(in);
        }
      };
    }
  }
}
