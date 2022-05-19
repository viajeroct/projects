package md2html;

import markup.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Md2Html {
    private final String inputFileName;
    private final String outputFileName;
    private final List<Tag> tags;

    public Md2Html(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        tags = List.of(
                new Tag("<<", ">>", "ins", CLASS_TYPE.CONT),
                new Tag("}}", "{{", "del", CLASS_TYPE.CONT),
                new Tag("**", "**", null, CLASS_TYPE.STRONG),
                new Tag("__", "__", null, CLASS_TYPE.STRONG),
                new Tag("--", "--", null, CLASS_TYPE.STRIKEOUT),
                new Tag("_", "_", null, CLASS_TYPE.EMPHASIS),
                new Tag("*", "*", null, CLASS_TYPE.EMPHASIS),
                new Tag("`", "`", "code", CLASS_TYPE.CONT)
        );
    }

    public static void main(String[] args) {
        Md2Html convertor = new Md2Html(args[0], args[1]);
        convertor.convertToHtml();
    }

    public int findIndexOf(String s, String find, int from) {
        for (int i = from; i + find.length() <= s.length(); i++) {
            if (s.startsWith(find, i)) {
                if (i + 1 < s.length() && s.charAt(i) == '_' &&
                        s.charAt(i + 1) == '_' && !find.equals("__")) {
                    continue;
                }
                if (i - 1 >= 0 && s.charAt(i) == '_' &&
                        s.charAt(i - 1) == '_' && !find.equals("__")) {
                    continue;
                }
                if (i + 1 < s.length() && s.charAt(i) == '*' &&
                        s.charAt(i + 1) == '*' && !find.equals("**")) {
                    continue;
                }
                if (i - 1 >= 0 && s.charAt(i) == '*' &&
                        s.charAt(i - 1) == '*' && !find.equals("**")) {
                    continue;
                }
                if (i - 1 >= 0 && s.charAt(i - 1) == '\\') {
                    continue;
                }
                return i;
            }
        }
        return -1;
    }

    public List<MarkRootAndText> convert(String s) {
        for (int i = 0; i < s.length(); i++) {
            for (Tag tag : tags) {
                if (i + tag.patternBegin.length() - 1 < s.length() &&
                        s.startsWith(tag.patternBegin, i)) {
                    int endOfTag = findIndexOf(s, tag.patternEnd, i + tag.patternBegin.length());
                    if (endOfTag != -1) {
                        ArrayList<MarkRootAndText> ans =
                                new ArrayList<>(convert(s.substring(0, i)));
                        final String substring = s.substring(i + tag.patternBegin.length(), endOfTag);
                        switch (tag.type) {
                            case CONT -> ans.add(new Cont(convert(substring), tag.patternReplace));
                            case STRONG -> ans.add(new Strong(convert(substring)));
                            case STRIKEOUT -> ans.add(new Strikeout(convert(substring)));
                            case EMPHASIS -> ans.add(new Emphasis(convert(substring)));
                        }
                        ans.addAll(convert(s.substring(endOfTag + tag.patternEnd.length())));
                        return ans;
                    }
                }
            }
        }
        return List.of(new Text(s));
    }

    public void convertToHtml() {
        try (
                //FastScanner2 in = new FastScanner2(inputFileName, StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(new FileInputStream(inputFileName), StandardCharsets.UTF_8)
                );
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(outputFileName), StandardCharsets.UTF_8)
                )
        ) {
            boolean eof = false;
            while (!eof) {
                StringBuilder currentParagraph = new StringBuilder();
                while (true) {
                    String currentLine = in.readLine();
                    if (currentLine == null) {
                        eof = true;
                        break;
                    }
                    if (currentLine.strip().isEmpty()) {
                        break;
                    }
                    currentParagraph.append(currentLine).append(System.lineSeparator());
                }
                if (currentParagraph.toString().strip().isEmpty()) {
                    continue;
                }
                replaceToHtmlCharacter("&", "&amp;", currentParagraph);
                replaceToHtmlCharacter("<", "&lt;", currentParagraph);
                replaceToHtmlCharacter(">", "&gt;", currentParagraph);
                int headingSize = getHeadingSize(currentParagraph);
                if (headingSize > 0) {
                    currentParagraph.replace(0, headingSize + 1, String.format("<h%d>", headingSize));
                } else {
                    currentParagraph = new StringBuilder("<p>").append(currentParagraph);
                }
                StringBuilder ans = new StringBuilder();
                new Paragraph(convert(currentParagraph.toString())).toHtml(ans);
                replaceToHtmlCharacter("\\*", "*", ans);
                replaceToHtmlCharacter("\\&lt;", "&lt;", ans);
                replaceToHtmlCharacter("\\&gt;", "&gt;", ans);
                replaceToHtmlCharacter("\\{", "{", ans);
                replaceToHtmlCharacter("\\}", "}", ans);
                out.write(ans.substring(0, ans.length() - System.lineSeparator().length()));
                if (headingSize > 0) {
                    out.write(String.format("</h%d>", headingSize));
                } else {
                    out.write("</p>");
                }
                out.newLine();
            }
        } catch (IOException e) {
            System.out.println("Can't read/write from/to input/output file: " + e.getMessage());
        }
    }

    public int getHeadingSize(StringBuilder line) {
        int headingSize = 0;
        for (int i = 0; i < line.length(); i++) {
            char symbol = line.charAt(i);
            if (symbol == '#') {
                headingSize++;
            } else {
                return symbol == ' ' ? headingSize : 0;
            }
        }
        return headingSize;
    }

    int check(int pos, StringBuilder src, String markCharacter, char pattern) {
        if (src.charAt(pos) == pattern && pos + 1 < src.length() && src.charAt(pos + 1) == pattern ||
                src.charAt(pos) == pattern && pos - 1 >= 0 && src.charAt(pos - 1) == pattern) {
            if (!(pos - 1 >= 0 && src.charAt(pos - 1) == '\\')) {
                return pos + markCharacter.length();
            }
        }
        return -1;
    }

    void replaceToHtmlCharacter(String markCharacter, String htmlCharacter, StringBuilder src) {
        int pos, last = 0;
        while (true) {
            pos = src.indexOf(markCharacter, last + markCharacter.length());
            if (pos == -1) {
                break;
            }
            int cur = check(pos, src, markCharacter, '>');
            if (cur != -1) {
                last = cur;
                continue;
            }
            cur = check(pos, src, markCharacter, '<');
            if (cur != -1) {
                last = cur;
                continue;
            }
            last = pos + markCharacter.length();
            src.replace(pos, pos + markCharacter.length(), htmlCharacter);
        }
    }
}
