import java.io.IOException;
import java.util.Arrays;

public class Reverse {
    public static void main(String[] args) {
        FastScanner in = new FastScanner();

        int rows = 0;
        String[] dt = new String[1];
        while (true) {
            try {
                if (in.isEOF()) {
                    break;
                }
                StringBuilder line = new StringBuilder();
                while (true) {
                    String s = in.next();
                    if (in.isNextLine()) {
                        break;
                    }
                    line.append(s);
                    line.append(" ");
                }
                if (rows >= dt.length) {
                    dt = Arrays.copyOf(dt, dt.length * 2);
                }
                dt[rows++] = line.toString();
            } catch (IOException e) {
                System.out.println("Can't input: " + e.getMessage());
            }
        }

        String[] temp = new String[1];
        for (int i = rows - 1; i >= 0; i--) {
            FastScanner line = new FastScanner(dt[i]);
            int amount = 0;
            while (true) {
                try {
                    if (line.isEOF()) {
                        break;
                    }
                    String cur = line.next();
                    if (cur == null) {
                        continue;
                    }
                    String number = cur;
                    if (amount >= temp.length) {
                        temp = Arrays.copyOf(temp, temp.length * 2);
                    }
                    temp[amount++] = number;
                } catch (IOException e) {
                    System.out.println("Can't read digit: " + e.getMessage());
                }
            }
            for (int j = amount - 1; j >= 0; j--) {
                System.out.print(temp[j] + " ");
            }
            System.out.println();
        }
    }
}
