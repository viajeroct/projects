package markup;

import java.util.List;

public class ListItem extends Root {
    public ListItem(List<? extends Top> list) {
        super(list);
    }

    @Override
    String getBeginHtmlBorder() {
        return "<li>";
    }

    @Override
    String getEndHtmlBorder() {
        return "</li>";
    }
}
