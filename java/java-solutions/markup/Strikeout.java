package markup;

import java.util.List;

public class Strikeout extends Root implements MarkRootAndText {
    public Strikeout(List<MarkRootAndText> list) {
        super(list);
    }

    @Override
    String getBeginMarkdownBorder() {
        return "~";
    }

    @Override
    String getEndMarkdownBorder() {
        return "~";
    }

    @Override
    String getBeginHtmlBorder() {
        return "<s>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</s>";
    }
}
