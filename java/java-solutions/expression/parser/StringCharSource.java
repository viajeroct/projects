package expression.parser;

public class StringCharSource implements CharSource {
    static private final char END = '\0';
    private final String data;
    private int pos;

    public StringCharSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char predictNext() {
        if (hasNext()) {
            return data.charAt(pos);
        }
        return END;
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }
}
