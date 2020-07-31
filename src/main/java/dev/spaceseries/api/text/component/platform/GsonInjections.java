package dev.spaceseries.api.text.component.platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Excluder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

final class GsonInjections {
  private GsonInjections() {
  }

  /**
   * Get a field from {@code klass} and make it accessible .
   *
   * @param klass containing class
   * @param name field name
   * @return the field
   * @throws NoSuchFieldException when thrown by {@link Class#getDeclaredField(String)}
   */
  public static Field field(final @NonNull Class<?> klass, final @NonNull String name) throws NoSuchFieldException {
    final Field field = klass.getDeclaredField(name);
    field.setAccessible(true);
    return field;
  }

  // Gson //

  @SuppressWarnings("unchecked")
  public static boolean injectGson(final @NonNull Gson existing, final @NonNull Consumer<GsonBuilder> accepter) {
    try {
      final Field factoriesField = field(Gson.class, "factories");
      final Field builderFactoriesField = field(GsonBuilder.class, "factories");
      final Field builderHierarchyFactoriesField = field(GsonBuilder.class, "hierarchyFactories");

      final GsonBuilder builder = new GsonBuilder();
      accepter.accept(builder);

      final List<TypeAdapterFactory> existingFactories = (List<TypeAdapterFactory>) factoriesField.get(existing);
      final List<TypeAdapterFactory> newFactories = new ArrayList<>((List<TypeAdapterFactory>) builderFactoriesField.get(builder));
      Collections.reverse(newFactories);
      newFactories.addAll((List<TypeAdapterFactory>) builderHierarchyFactoriesField.get(builder));

      final List<TypeAdapterFactory> modifiedFactories = new ArrayList<>(existingFactories);

      // the excluder must precede all adapters that handle user-defined types
      final int index = findExcluderIndex(modifiedFactories);

      Collections.reverse(newFactories);
      for(final TypeAdapterFactory newFactory : newFactories) {
        modifiedFactories.add(index, newFactory);
      }

      factoriesField.set(existing, modifiedFactories);
      return true;
    } catch(final NoSuchFieldException | IllegalAccessException ex) {
      return false;
    }
  }

  private static int findExcluderIndex(final @NonNull List<TypeAdapterFactory> factories) {
    for(int i = 0, size = factories.size(); i < size; i++) {
      final TypeAdapterFactory factory = factories.get(i);
      if(factory instanceof Excluder) {
        return i + 1;
      }
    }
    return 0;
  }
}
