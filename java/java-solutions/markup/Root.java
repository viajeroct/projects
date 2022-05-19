package markup;

import java.util.List;

public abstract class Root implements Top {
    private final List<? extends Top> list;

    public Root(List<? extends Top> list) {
        this.list = list;
    }

    @Override
    public void toMarkdown(StringBuilder answer) {
        answer.append(getBeginMarkdownBorder());
        for (MarkdownTop it : list) {
            it.toMarkdown(answer);
        }
        answer.append(getEndMarkdownBorder());
    }

    @Override
    public void toHtml(StringBuilder answer) {
        answer.append(getBeginHtmlBorder());
        for (HtmlTop it : list) {
            it.toHtml(answer);
        }
        answer.append(getEndHtmlBorder());
    }

    String getBeginMarkdownBorder() {
        return "";
    }

    String getEndMarkdownBorder() {
        return "";
    }

    String getBeginHtmlBorder() {
        return "";
    }

    String getEndHtmlBorder() {
        return "";
    }
}
