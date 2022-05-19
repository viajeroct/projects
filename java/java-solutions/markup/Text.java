package markup;

public class Text implements MarkRootAndText {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void toMarkdown(StringBuilder answer) {
        answer.append(text);
    }

    @Override
    public void toHtml(StringBuilder answer) {
        answer.append(text);
    }
}
