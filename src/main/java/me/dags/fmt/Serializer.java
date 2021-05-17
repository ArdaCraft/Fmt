package me.dags.fmt;

import com.google.common.reflect.TypeToken;
import java.util.function.Function;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

/**
 * @author dags <dags@dags.me>
 */
final class Serializer implements TypeSerializer<Format> {

    private static final String INFO = "info";
    private static final String SUBDUED = "subdued";
    private static final String STRESS = "stress";
    private static final String ERROR = "error";
    private static final String WARN = "warn";

    Serializer() {}

    @Override
    public Format deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return deserialize(value);
    }

    @Override
    public void serialize(TypeToken<?> type, Format format, ConfigurationNode value) throws ObjectMappingException {
        serialize(format, value);
    }

    static Format deserialize(ConfigurationNode value) {
        Format.Builder builder = Format.builder();
        builder.info(getFormat(value.node(INFO)));
        builder.subdued(getFormat(value.node(SUBDUED)));
        builder.stress(getFormat(value.node(STRESS)));
        builder.error(getFormat(value.node(ERROR)));
        builder.warn(getFormat(value.node(WARN)));
        return builder.build();
    }

    static void serialize(Format format, ConfigurationNode value) {
        setFormat(format.info, value.node(INFO));
        setFormat(format.subdued, value.node(SUBDUED));
        setFormat(format.stress, value.node(STRESS));
        setFormat(format.error, value.node(ERROR));
        setFormat(format.warn, value.node(WARN));
    }

    private static TextFormat getFormat(ConfigurationNode node) {
        if (node.virtual()) {
            return TextFormat.NONE;
        }

        String colorId = node.node("color").getString("");
        //TODO check if adventureRegistry is what we want here.
        TextColor color = Sponge.registry().adventureRegistry(TextColor.class, colorId).orElse(null);

        TextStyle style = TextStyles.of();
        style = applyNonNull(style, style::bold, (Boolean) node.node("bold").((Object) null));
        style = applyNonNull(style, style::italic, (Boolean) node.node("italic").getValue((Object) null));
        style = applyNonNull(style, style::underline, (Boolean) node.node("underline").getValue((Object) null));
        style = applyNonNull(style, style::obfuscated, (Boolean) node.node("obfuscated").getValue((Object) null));
        style = applyNonNull(style, style::strikethrough, (Boolean) node.node("strikethrough").getValue((Object) null));

        TextFormat format = TextFormat.of(style);
        return applyNonNull(format, format::color, color);
    }

    private static void setFormat(TextFormat format, ConfigurationNode node) {
        if (format.getColor() != TextColors.NONE) {
            node.getNode("color").setValue(format.getColor().getName());
        }

        format.getStyle().isBold().ifPresent(node.getNode("bold")::setValue);
        format.getStyle().isItalic().ifPresent(node.getNode("italic")::setValue);
        format.getStyle().isObfuscated().ifPresent(node.getNode("obfuscated")::setValue);
        format.getStyle().hasStrikethrough().ifPresent(node.getNode("strikethrough")::setValue);
        format.getStyle().hasUnderline().ifPresent(node.getNode("underline")::setValue);
    }

    private static <T, V> T applyNonNull(T style, Function<V, T> func, V value) {
        if (value != null) {
            return func.apply(value);
        }
        return style;
    }
}
