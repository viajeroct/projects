package markup;

import java.util.List;

public class OrderedList extends Root {
    public OrderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    String getBeginHtmlBorder() {
        return "<ol>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</ol>";
    }
}
