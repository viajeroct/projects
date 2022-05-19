package markup;

import java.util.List;

public class UnorderedList extends Root {
    public UnorderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    String getBeginHtmlBorder() {
        return "<ul>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</ul>";
    }
}
