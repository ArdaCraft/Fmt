package me.dags.fmt;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

/**
 * @author dags <dags@dags.me>
 */
public final class Fmt {

    private static final Path root;
    private static final Format fmt;
    private static final Map<String, Format> formats = Maps.newConcurrentMap();

    public static final TypeSerializer<Format> SERIALIZER = new Serializer();
    public static final TypeToken<Format> TYPE_TOKEN = TypeToken.of(Format.class);

    static {
        root = Sponge.getGame().getGameDirectory().resolve("config").resolve("fmt");
        Format format = read("global");
        fmt = format != null ? format : Format.builder().build();
        formats.put("global", fmt);
        write(fmt, "global");
        ConfigurationOptions.defaults().getSerializers().registerType(Fmt.TYPE_TOKEN, Fmt.SERIALIZER);
    }

    private Fmt() {}

    public static void init(Format override) {
        getActivePlugin().ifPresent(plugin -> init(plugin.getId(), override));
    }

    public static void init(String id, Format override) {
        if (get(id).equals(fmt) && !override.equals(fmt)) {
            write(override, id);
        }
    }

    public static Format get(String id) {
        return get(id, fmt);
    }

    public static Format get(String id, Format defaultFormat) {
        Format format = formats.get(id);
        if (format == null) {
            format = read(id);
            if (format == null) {
                format = defaultFormat;
                write(format, id);
            }
            formats.put(id, format);
        }
        return format;
    }

    public static Formatter fmt() {
        return getActivePlugin().map(PluginContainer::getId).map(Fmt::get).orElse(fmt).fmt();
    }

    public static Format copy() {
        return fmt.copy();
    }

    public static Formatter info(Object input, Object... args) {
        return fmt().info(input, args);
    }

    public static Formatter subdued(Object input, Object... args) {
        return fmt().subdued(input, args);
    }

    public static Formatter stress(Object input, Object... args) {
        return fmt().stress(input, args);
    }

    public static Formatter error(Object input, Object... args) {
        return fmt().error(input, args);
    }

    public static Formatter warn(Object input, Object... args) {
        return fmt().warn(input, args);
    }

    public static <T> Formatter info(Iterable<T> input, String separator, BiConsumer<Formatter, T> consumer) {
        return fmt().info(input, separator, consumer);
    }

    public static <T> Formatter subdued(Iterable<T> input, String separator, BiConsumer<Formatter, T> consumer) {
        return fmt().subdued(input, separator, consumer);
    }

    public static <T> Formatter stress(Iterable<T> input, String separator, BiConsumer<Formatter, T> consumer) {
        return fmt().stress(input, separator, consumer);
    }

    public static <T> Formatter error(Iterable<T> input, String separator, BiConsumer<Formatter, T> consumer) {
        return fmt().error(input, separator, consumer);
    }

    public static <T> Formatter warn(Iterable<T> input, String separator, BiConsumer<Formatter, T> consumer) {
        return fmt().warn(input, separator, consumer);
    }

    private static Optional<PluginContainer> getActivePlugin() {
        if (!Sponge.getServer().isMainThread()) {
            return Optional.empty();
        }
        return Sponge.getCauseStackManager().getCurrentCause().last(PluginContainer.class);
    }

    private static Format read(String identifier) {
        Path path = root.resolve(String.format("%s.conf", identifier));

        if (!Files.exists(path)) {
            return null;
        }

        try {
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(path).build();
            CommentedConfigurationNode node = loader.load();
            return Serializer.deserialize(node);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void write(Format format, String identifier) {
        try {
            Files.createDirectories(root);
            Path path = root.resolve(String.format("%s.conf", identifier));

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(path).build();
            CommentedConfigurationNode node = loader.createEmptyNode();
            Serializer.serialize(format, node);
            loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
