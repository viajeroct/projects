package markup;

import java.util.List;

public class Strong extends Root implements MarkRootAndText {
    public Strong(List<MarkRootAndText> list) {
        super(list);
    }

    @Override
    String getBeginMarkdownBorder() {
        return "__";
    }

    @Override
    String getEndMarkdownBorder() {
        return "__";
    }

    @Override
    String getBeginHtmlBorder() {
        return "<strong>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</strong>";
    }
}
