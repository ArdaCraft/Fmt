package me.dags.fmt;

import org.spongepowered.api.text.format.*;

/**
 * @author dags <dags@dags.me>
 */
public final class Format {

    final TextFormat info;
    final TextFormat subdued;
    final TextFormat stress;
    final TextFormat error;
    final TextFormat warn;

    private Format(Builder builder) {
        this.info = builder.info;
        this.subdued = builder.subdued;
        this.stress = builder.stress;
        this.error = builder.error;
        this.warn = builder.warn;
    }

    public Formatter start() {
        return new Formatter(this);
    }

    public PagFormatter list() {
        return new PagFormatter(this);
    }

    public Formatter info(Object input, Object... args) {
        return new Formatter(this).info(input, args);
    }

    public Formatter subdued(Object input, Object... args) {
        return new Formatter(this).subdued(input, args);
    }

    public Formatter stress(Object input, Object... args) {
        return new Formatter(this).stress(input, args);
    }

    public Formatter error(Object input, Object... args) {
        return new Formatter(this).error(input, args);
    }

    public Formatter warn(Object input, Object... args) {
        return new Formatter(this).warn(input, args);
    }

    public Format copy() {
        return toBuilder().build();
    }

    public Format.Builder toBuilder() {
        return new Builder()
                .info(info.getColor(), info.getStyle())
                .stress(stress.getColor(), stress.getStyle())
                .subdued(subdued.getColor(), subdued.getStyle())
                .error(error.getColor(), error.getStyle())
                .warn(warn.getColor(), warn.getStyle());
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("info=").append(info)
                .append(", subdued=").append(subdued)
                .append(", stress=").append(stress)
                .append(", error=").append(error)
                .append(", warn=").append(warn)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        TextFormat info = TextFormat.of(TextColors.WHITE);
        TextFormat subdued = TextFormat.of(TextStyles.ITALIC);
        TextFormat stress = TextFormat.of(TextColors.GREEN);
        TextFormat error = TextFormat.of(TextColors.GRAY);
        TextFormat warn = TextFormat.of(TextColors.RED);

        public Builder info(TextFormat format) {
            this.info = format;
            return this;
        }

        public Builder info(TextColor color, TextStyle... style) {
            info = TextFormat.of(color, TextStyles.of(style));
            return this;
        }

        public Builder info(TextStyle... style) {
            info = TextFormat.of(TextColors.NONE, TextStyles.of(style));
            return this;
        }

        public Builder subdued(TextFormat format) {
            this.subdued = format;
            return this;
        }

        public Builder subdued(TextStyle... style) {
            subdued = TextFormat.of(TextStyles.of(style));
            return this;
        }

        public Builder subdued(TextColor color, TextStyle... style) {
            subdued = TextFormat.of(color, TextStyles.of(style));
            return this;
        }

        public Builder stress(TextFormat format) {
            this.stress = format;
            return this;
        }

        public Builder stress(TextStyle... style) {
            stress = TextFormat.of(TextStyles.of(style));
            return this;
        }

        public Builder stress(TextColor color, TextStyle... style) {
            stress = TextFormat.of(color, TextStyles.of(style));
            return this;
        }

        public Builder error(TextFormat format) {
            this.error = format;
            return this;
        }

        public Builder error(TextStyle... style) {
            error = TextFormat.of(TextStyles.of(style));
            return this;
        }

        public Builder error(TextColor color, TextStyle... style) {
            error = TextFormat.of(color, TextStyles.of(style));
            return this;
        }

        public Builder warn(TextFormat format) {
            this.warn = format;
            return this;
        }

        public Builder warn(TextStyle... style) {
            warn = TextFormat.of(TextStyles.of(style));
            return this;
        }

        public Builder warn(TextColor color, TextStyle... style) {
            warn = TextFormat.of(color, TextStyles.of(style));
            return this;
        }

        public Format build() {
            return new Format(this);
        }
    }
}
