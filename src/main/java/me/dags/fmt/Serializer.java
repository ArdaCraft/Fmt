package me.dags.fmt;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.*;

import java.util.function.Function;

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
        builder.info(getFormat(value.getNode(INFO)));
        builder.subdued(getFormat(value.getNode(SUBDUED)));
        builder.stress(getFormat(value.getNode(STRESS)));
        builder.error(getFormat(value.getNode(ERROR)));
        builder.warn(getFormat(value.getNode(WARN)));
        return builder.build();
    }

    static void serialize(Format format, ConfigurationNode value) {
        setFormat(format.info, value.getNode(INFO));
        setFormat(format.subdued, value.getNode(SUBDUED));
        setFormat(format.stress, value.getNode(STRESS));
        setFormat(format.error, value.getNode(ERROR));
        setFormat(format.warn, value.getNode(WARN));
    }

    private static TextFormat getFormat(ConfigurationNode node) {
        if (node.isVirtual()) {
            return TextFormat.NONE;
        }

        String colorId = node.getNode("color").getString("");
        TextColor color = Sponge.getRegistry().getType(TextColor.class, colorId).orElse(null);

        TextStyle style = TextStyles.of();
        style = applyNonNull(style, style::bold, (Boolean) node.getNode("bold").getValue((Object) null));
        style = applyNonNull(style, style::italic, (Boolean) node.getNode("italic").getValue((Object) null));
        style = applyNonNull(style, style::underline, (Boolean) node.getNode("underline").getValue((Object) null));
        style = applyNonNull(style, style::obfuscated, (Boolean) node.getNode("obfuscated").getValue((Object) null));
        style = applyNonNull(style, style::strikethrough, (Boolean) node.getNode("strikethrough").getValue((Object) null));

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
