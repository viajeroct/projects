package markup;

import java.util.List;

public class Emphasis extends Root implements MarkRootAndText {
    public Emphasis(List<MarkRootAndText> list) {
        super(list);
    }

    @Override
    String getBeginMarkdownBorder() {
        return "*";
    }

    @Override
    String getEndMarkdownBorder() {
        return "*";
    }

    @Override
    String getBeginHtmlBorder() {
        return "<em>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</em>";
    }
}
