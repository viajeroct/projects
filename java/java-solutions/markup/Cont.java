package markup;

import java.util.List;

public class Cont extends Root implements MarkRootAndText {
    private final String border;

    public Cont(List<MarkRootAndText> list, String border) {
        super(list);
        this.border = border;
    }

    @Override
    String getBeginMarkdownBorder() {
        return String.format("<%s>", border);
    }

    @Override
    String getEndMarkdownBorder() {
        return String.format("<%s>", border);
    }

    @Override
    String getBeginHtmlBorder() {
        return String.format("<%s>", border);
    }

    @Override
    String getEndHtmlBorder() {
        return String.format("</%s>", border);
    }
}
