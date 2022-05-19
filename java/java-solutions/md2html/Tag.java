package md2html;

public class Tag {
    public final String patternBegin;
    public final String patternEnd;
    public final String patternReplace;
    public final CLASS_TYPE type;

    public Tag(String patternBegin, String patternEnd, String patternReplace, CLASS_TYPE type) {
        this.patternBegin = patternBegin;
        this.patternEnd = patternEnd;
        this.patternReplace = patternReplace;
        this.type = type;
    }
}
