package me.dags.fmt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Tamer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public final class Formatter implements TextRepresentable {

    private final Format format;
    private final Formatter parent;
    private final Text.Builder builder = Text.builder();

    private boolean empty = true;

    Formatter(Format format) {
        this.format = format;
        this.parent = null;
    }

    private Formatter(Formatter parent, Format format) {
        this.parent = parent;
        this.format = format;
    }

    boolean isEmpty() {
        return empty;
    }

    @Override
    public Text toText() {
        return builder.build();
    }

    public Text build() {
        return toText();
    }

    public HoverAction<Text> toHover() {
        return TextActions.showText(toText());
    }

    public Title title() {
        return title(null, null, null);
    }

    public Title title(int transition) {
        return title(transition, transition, transition);
    }

    public Title title(int fade, int stay) {
        return title(fade, stay, fade);
    }

    public Title title(@Nullable Integer fadeIn, @Nullable Integer stay, @Nullable Integer fadeOut) {
        Text main = parent == null ? this.build() : parent.build();
        Text sub = parent == null ? null : this.build();
        return Title.builder()
                .title(main)
                .subtitle(sub)
                .fadeIn(fadeIn)
                .fadeOut(fadeOut)
                .stay(stay)
                .build();
    }

    public Formatter subtitle() {
        return new Formatter(this, format);
    }

    public Formatter line() {
        if (!empty) {
            append(Text.NEW_LINE);
        }
        return this;
    }

    public Formatter command(String pattern, Object... args) {
        return action(TextActions.runCommand(String.format(pattern, namedArgs(args))));
    }

    public Formatter suggest(String pattern, Object... args) {
        return action(TextActions.suggestCommand(String.format(pattern, namedArgs(args))));
    }

    public Formatter insert(String pattern, Object... args) {
        return action(TextActions.insertText(String.format(pattern, namedArgs(args))));
    }

    public Formatter url(String url) {
        try {
            return action(TextActions.openUrl(new URL(url)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return this;
        }
    }

    public Formatter action(TextAction action) {
        if (action instanceof ShiftClickAction) {
            empty = false;
            builder.onShiftClick((ShiftClickAction) action);
        } else if (action instanceof ClickAction) {
            empty = false;
            builder.onClick((ClickAction) action);
        } else if (action instanceof HoverAction) {
            empty = false;
            builder.onHover((HoverAction) action);
        }
        return this;
    }

    public <T> Formatter list(Iterable<T> iterable, Consumer<Formatter> separator, BiConsumer<Formatter, T> consumer) {
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            consumer.accept(this, iterator.next());
            if (iterator.hasNext()) {
                separator.accept(this);
            }
        }
        return this;
    }

    public Formatter append(Text text) {
        empty = false;
        builder.append(text);
        return this;
    }

    public Formatter info(Object input, Object... args) {
        return append(apply(format.info, input, args));
    }

    public <T> Formatter info(Iterable<T> iterable, String separator, BiConsumer<Formatter, T> consumer) {
        return list(iterable, f -> f.info(separator), consumer);
    }

    public Formatter subdued(Object input, Object... args) {
        return append(apply(format.subdued, input, args));
    }

    public <T> Formatter subdued(Iterable<T> iterable, String separator, BiConsumer<Formatter, T> consumer) {
        return list(iterable, f -> f.subdued(separator), consumer);
    }

    public Formatter stress(Object input, Object... args) {
        return append(apply(format.stress, input, args));
    }

    public <T> Formatter stress(Iterable<T> iterable, String separator, BiConsumer<Formatter, T> consumer) {
        return list(iterable, f -> f.stress(separator), consumer);
    }

    public Formatter error(Object input, Object... args) {
        return append(apply(format.error, input, args));
    }

    public <T> Formatter error(Iterable<T> iterable, String separator, BiConsumer<Formatter, T> consumer) {
        return list(iterable, f -> f.error(separator), consumer);
    }

    public Formatter warn(Object input, Object... args) {
        return append(apply(format.warn, input, args));
    }

    public <T> Formatter warn(Iterable<T> iterable, String separator, BiConsumer<Formatter, T> consumer) {
        return list(iterable, f -> f.warn(separator), consumer);
    }

    public Formatter log() {
        return tell(Sponge.getServer().getConsole());
    }

    public Formatter broadcast() {
        return tell(Sponge.getServer().getBroadcastChannel());
    }

    public Formatter tell(MessageReceiver receiver) {
        receiver.sendMessage(build());
        return this;
    }

    public Formatter tell(Iterable<? extends MessageReceiver> receivers) {
        Text message = build();
        for (MessageReceiver receiver : receivers) {
            receiver.sendMessage(message);
        }
        return this;
    }

    public Formatter tell(MessageReceiver... receivers) {
        Text message = build();
        for (MessageReceiver receiver : receivers) {
            receiver.sendMessage(message);
        }
        return this;
    }

    public Formatter tell(MessageChannel... messageChannels) {
        Text message = build();
        for (MessageChannel channel : messageChannels) {
            channel.send(message);
        }
        return this;
    }

    public Formatter tell(ChatType chatType, ChatTypeMessageReceiver... receivers) {
        Text message = build();
        for (ChatTypeMessageReceiver receiver : receivers) {
            receiver.sendMessage(chatType, message);
        }
        return this;
    }

    public Formatter tell(ChatType chatType, MessageChannel... messageChannels) {
        Text message = build();
        for (MessageChannel channel : messageChannels) {
            channel.send(message, chatType);
        }
        return this;
    }

    public Formatter tellPermitted(String permission) {
        return tell(MessageChannel.permission(permission));
    }

    public Formatter title(Player... receivers) {
        return title(null, null, null, receivers);
    }

    public Formatter title(int fade, Player... receivers) {
        return title(fade, null, fade, receivers);
    }

    public Formatter title(int fade, int stay, Player... receivers) {
        return title(fade, stay, fade, receivers);
    }

    public Formatter title(@Nullable Integer fadeIn, @Nullable Integer stay, @Nullable Integer fadeOut, Player... receivers) {
        Title title = title(fadeIn, stay, fadeOut);
        for (Player player : receivers) {
            player.sendTitle(title);
        }
        return this;
    }

    private static Text apply(TextFormat format, Object text, Object... args) {
        if (text instanceof TextRepresentable) {
            TextRepresentable representable = (TextRepresentable) text;
            return representable.toText().toBuilder().format(format).build();
        }

        if (text instanceof String && args.length > 0) {
            String string = String.format(text.toString(), namedArgs(args));
            return Text.builder(string).format(format).build();
        }

        text = namedObject(text);

        return Text.builder(text.toString()).format(format).build();
    }

    private static Object[] namedArgs(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = namedObject(args[i]);
        }
        return args;
    }

    private static Object namedObject(Object o) {
        if (o instanceof Tamer) {
            return ((Tamer) o).getName();
        }
        if (o instanceof CommandSource) {
            return ((CommandSource) o).getName();
        }
        if (o instanceof World) {
            return ((World) o).getName();
        }
        if (o instanceof CatalogType) {
            return ((CatalogType) o).getName();
        }
        return o;
    }
}
