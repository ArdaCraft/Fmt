package me.dags.fmt;

import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */
public final class PagFormatter {

    private final Format format;
    private final Formatter title;
    private final Formatter header;
    private final Formatter footer;
    private final Formatter padding;
    private final List<Text> lines = new LinkedList<>();

    private Formatter line;
    private int linesPerPage = 10;

    PagFormatter(Format format) {
        this.format = format;
        line = format.start();
        title = format.start();
        header = format.start();
        footer = format.start();
        padding = format.start();
    }

    public Formatter line() {
        flush();
        return line;
    }

    public Formatter title() {
        return title;
    }

    public Formatter header() {
        return header;
    }

    public Formatter footer() {
        return footer;
    }

    public Formatter padding() {
        return padding;
    }

    public PagFormatter lines(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        return this;
    }

    public PagFormatter sort(boolean ignoreCase) {
        Collections.sort(lines, (t1, t2) -> {
            String s1 = t1.toPlain();
            String s2 = t2.toPlain();
            if (ignoreCase) {
                s1 = s1.toLowerCase();
                s2 = s2.toLowerCase();
            }
            return s1.compareTo(s2);
        });
        return this;
    }

    public PaginationList build() {
        flush();
        PaginationList.Builder builder = PaginationList.builder();
        apply(title, builder::title);
        apply(header, builder::header);
        apply(footer, builder::footer);
        apply(padding, builder::padding);
        builder.contents(lines);
        builder.linesPerPage(linesPerPage);
        return builder.build();
    }

    private void flush() {
        if (!line.isEmpty()) {
            lines.add(line.build());
            line = format.start();
        }
    }

    private void apply(Formatter formatter, Consumer<Text> consumer) {
        if (!formatter.isEmpty()) {
            consumer.accept(formatter.build());
        }
    }
}
