package md2html;

import java.io.*;
import java.nio.charset.Charset;

class FastScanner2 implements AutoCloseable {
    private Reader in;

    private boolean EOF;
    private char[] buffer;
    private int position, size;
    private char ch;
    private boolean nextLine = false;

    public FastScanner2() {
        in = new InputStreamReader(System.in);
        init();
    }

    public FastScanner2(String s) {
        in = new StringReader(s);
        init();
    }

    public FastScanner2(String filename, Charset set) throws FileNotFoundException {
        in = new InputStreamReader(new FileInputStream(filename), set);
        init();
    }

    private void init() {
        position = size = 0;
        buffer = new char[1024];
        EOF = false;
    }

    private void getNextSymbol() throws IOException {
        if (position == size) {
            size = in.read(buffer);
            position = 0;
        }
        if (size == -1) {
            EOF = true;
            return;
        }
        ch = buffer[position++];
    }

    private boolean isWord(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '\'' || Character.getType(ch) == Character.DASH_PUNCTUATION;
    }

    public boolean isEOF() {
        return EOF;
    }

    private void skipSpaces() throws IOException {
        while (!isWord(ch) && !isEOF()) {
            if (ch == '\n' || System.lineSeparator().equals(Character.toString(ch))) {
                nextLine = true;
                getNextSymbol();
                break;
            }
            getNextSymbol();
        }
    }

    public String next() throws IOException {
        nextLine = false;
        StringBuilder ans = new StringBuilder();
        skipSpaces();
        if (isNextLine()) {
            return null;
        }
        if (!isWord(ch)) {
            return null;
        }
        do {
            ans.append(ch);
            getNextSymbol();
        } while (isWord(ch) && !isEOF());
        return ans.toString();
    }

    public boolean isNextLine() {
        return nextLine;
    }

    String nextLine() throws IOException {
        StringBuilder ans = new StringBuilder();
        if (!isWord(ch)) {
            getNextSymbol();
        }
        do {
            ans.append(ch);
            getNextSymbol();
        } while (ch != '\n' && !System.lineSeparator().equals(Character.toString(ch)) && !isEOF());
        return ans.toString();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
