package expression.parser;

public interface CharSource {
    boolean hasNext();

    char next();

    void prev(int x);

    char predictNext();
}
