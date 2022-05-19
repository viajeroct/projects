package expression;

public class UnaryZeroes extends UnaryOperation {
    private final boolean l0_type;

    public UnaryZeroes(Parent innerElement, boolean l0_type) {
        super(innerElement);
        this.l0_type = l0_type;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public int eval(int x) {
        StringBuilder cur = new StringBuilder(Integer.toBinaryString(x));
        while (cur.length() < 32) {
            cur.insert(0, "0");
        }
        int offset = 0;
        if (l0_type) {
            for (int i = 0; i < 32; i++) {
                if (cur.charAt(i) != '0') {
                    break;
                }
                offset++;
            }
        } else {
            for (int i = 31; i >= 0; i--) {
                if (cur.charAt(i) != '0') {
                    break;
                }
                offset++;
            }
        }
        return offset;
    }

    @Override
    public String getPrefix() {
        return l0_type ? "l0" : "t0";
    }
}
