package dev.spaceseries.api.text.component.serializer.gson;

import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.util.Codec;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Adapter to convert between modern and legacy hover event formats.
 */
public interface LegacyHoverEventSerializer {
  /**
   * Convert a legacy hover event {@code show_item} value to its modern format.
   *
   * @param input component whose plain-text value is a SNBT string
   * @return the deserialized event
   * @throws IOException if the input is improperly formatted
   */
  HoverEvent.@NonNull ShowItem deserializeShowItem(final @NonNull Component input) throws IOException;

  /**
   * Convert a legacy hover event {@code show_entity} value to its modern format.
   *
   * @param input component whose plain-text value is a SNBT string
   * @param componentDecoder A decoder that can take a JSON string and return a deserialized component
   * @return the deserialized event
   * @throws IOException if the input is improperly formatted
   */
  HoverEvent.@NonNull ShowEntity deserializeShowEntity(final @NonNull Component input, final Codec.Decoder<Component, String, ? extends RuntimeException> componentDecoder) throws IOException;

  /**
   * Convert a modern hover event {@code show_item} value to its legacy format.
   *
   * @param input modern hover event
   * @return component with the legacy value as a SNBT string
   * @throws IOException if the input is improperly formatted
   */
  @NonNull Component serializeShowItem(final HoverEvent.@NonNull ShowItem input) throws IOException;

  /**
   * Convert a modern hover event {@code show_entity} value to its legacy format.
   *
   * @param input modern hover event
   * @param componentEncoder An encoder that can take a {@link Component} and return a JSON string
   * @return component with the legacy value as a SNBT string
   * @throws IOException if the input is improperly formatted
   */
  @NonNull Component serializeShowEntity(final HoverEvent.@NonNull ShowEntity input, final Codec.Encoder<Component, String, ? extends RuntimeException> componentEncoder) throws IOException;
}
