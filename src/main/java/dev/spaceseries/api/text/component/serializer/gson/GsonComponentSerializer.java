package dev.spaceseries.api.text.component.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.function.UnaryOperator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.util.Buildable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A gson component serializer.
 *
 * <p>Use {@link Builder#downsampleColors()} to support platforms
 * that do not understand hex colors that were introduced in Minecraft 1.16.</p>
 */
public interface GsonComponentSerializer extends ComponentSerializer<Component, Component, String>, Buildable<GsonComponentSerializer, GsonComponentSerializer.Builder> {
    /**
     * Gets a component serializer for gson serialization and deserialization.
     *
     * @return a gson component serializer
     */
    static @NonNull GsonComponentSerializer gson() {
        return GsonComponentSerializerImpl.INSTANCE;
    }

    /**
     * Gets a component serializer for gson serialization and deserialization.
     *
     * <p>Hex colors are coerced to the nearest named color, and legacy hover events are
     * emitted for action {@link net.kyori.adventure.text.event.HoverEvent.Action#SHOW_TEXT}.</p>
     *
     * @return a gson component serializer
     */
    static @NonNull GsonComponentSerializer colorDownsamplingGson() {
        return GsonComponentSerializerImpl.LEGACY_INSTANCE;
    }

    /**
     * Creates a new {@link GsonComponentSerializer.Builder}.
     *
     * @return a builder
     */
    static Builder builder() {
        return new GsonComponentSerializerImpl.BuilderImpl();
    }

    /**
     * Gets the underlying gson serializer.
     *
     * @return a gson serializer
     */
    @NonNull Gson serializer();

    /**
     * Gets the underlying gson populator.
     *
     * @return a gson populator
     */
    @NonNull UnaryOperator<GsonBuilder> populator();

    /**
     * A builder for {@link GsonComponentSerializer}.
     */
    interface Builder extends Buildable.AbstractBuilder<GsonComponentSerializer> {
        /**
         * Sets that the serializer should downsample hex colors to named colors.
         *
         * @return this builder
         */
        @NonNull Builder downsampleColors();

        /**
         * Sets a serializer that will be used to interpret legacy hover event {@code value} payloads
         *
         * @param serializer serializer
         * @return this builder
         */
        @NonNull Builder legacyHoverEventSerializer(final @NonNull LegacyHoverEventSerializer serializer);

        /**
         * Output a legacy hover event {@code value} in addition to the modern {@code contents}
         *
         *
         * <p>A {@link #legacyHoverEventSerializer(LegacyHoverEventSerializer) legacy hover serializer} must also be set
         * to serialize any hover events beyond those with action {@link net.kyori.adventure.text.event.HoverEvent.Action#SHOW_TEXT}</p>
         *
         * @return this builder
         */
        @NonNull Builder emitLegacyHoverEvent();

        /**
         * Builds the serializer.
         *
         * @return the built serializer
         */
        @Override
        @NonNull GsonComponentSerializer build();
    }
}
