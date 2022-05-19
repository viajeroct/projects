import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TaskH {
    public static void main(String[] args) throws IOException {
        FastScanner in = new FastScanner();

        int n = in.nextInt();
        int max = Integer.MIN_VALUE;
        int[] a = new int[n];
        int total = 0;
        int[] pref = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
            total += a[i];
            max = Math.max(max, a[i]);
            pref[i] = (i > 0 ? pref[i - 1] : 0) + a[i];
        }

        int[] already = new int[total + 1];
        int q = in.nextInt();
        for (int i = 0; i < q; i++) {
            int t = in.nextInt();
            if (t < max) {
                System.out.println("Impossible");
                continue;
            }

            if (already[t] != 0) {
                System.out.println(already[t]);
                continue;
            }

            int last = -1, ans = 0;
            while (last < n - 1) {
                int l = Math.max(last, 0), r = n;
                while (r - l > 1) {
                    int m = (l + r) / 2;
                    int cur = (last >= 0 ? pref[last] : 0);
                    if (pref[m] - cur <= t) {
                        l = m;
                    } else {
                        r = m;
                    }
                }
                last = l;
                ans++;
            }

            already[t] = ans;
            System.out.println(ans);
        }
    }

    static class FastScanner {
        Reader in;

        boolean EOF;
        char[] buffer;
        int position, size;
        char ch;

        FastScanner() {
            in = new InputStreamReader(System.in);
            init();
        }

        void init() {
            position = size = 0;
            buffer = new char[1024];
            EOF = false;
        }

        void getNextSymbol() throws IOException {
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

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }

        boolean isWord(char ch) {
            return Character.isDigit(ch);
        }

        boolean isEOF() {
            return !EOF;
        }

        void skipSpaces() throws IOException {
            while (!isWord(ch) && isEOF()) {
                getNextSymbol();
            }
        }

        String next() throws IOException {
            StringBuilder ans = new StringBuilder();
            skipSpaces();
            if (!isWord(ch)) {
                return null;
            }
            do {
                ans.append(ch);
                getNextSymbol();
            } while (isWord(ch) && isEOF());
            return ans.toString();
        }
    }
}
