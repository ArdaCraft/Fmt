package me.dags.fmt;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.action.*;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.title.Title;

import javax.annotation.Nullable;

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

    public Formatter append(Text text) {
        empty = false;
        builder.append(text);
        return this;
    }

    public Formatter info(Object input, Object... args) {
        return append(apply(format.info, input, args));
    }

    public Formatter subdued(Object input, Object... args) {
        return append(apply(format.subdued, input, args));
    }

    public Formatter stress(Object input, Object... args) {
        return append(apply(format.stress, input, args));
    }

    public Formatter error(Object input, Object... args) {
        return append(apply(format.error, input, args));
    }

    public Formatter warn(Object input, Object... args) {
        return append(apply(format.warn, input, args));
    }

    public Formatter log() {
        return tell(Sponge.getServer().getConsole());
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

    public Formatter tell(ChatType chatType, Player... receivers) {
        Text message = build();
        for (Player receiver : receivers) {
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

        if (args.length > 0) {
            String string = String.format(text.toString(), args);
            return Text.builder(string).format(format).build();
        }

        return Text.builder(text.toString()).format(format).build();
    }
}
