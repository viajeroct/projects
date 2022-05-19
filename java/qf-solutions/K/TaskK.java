import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

public class TaskK {
    public static void main(String[] args) {
        try (FastScanner in = new FastScanner()) {
            PrintWriter out = new PrintWriter(System.out, false);

            int[] letRow = new int[40];
            int[] letCol = new int[40];
            int size = 0;
            int n = in.nextInt(), m = in.nextInt();
            char[][] dt = new char[n][m];
            int rowA = 0, colA = 0;
            for (int i = 0; i < n; i++) {
                String cur = in.next();
                for (int j = 0; j < m; j++) {
                    dt[i][j] = cur.charAt(j);
                    if (dt[i][j] == 'A') {
                        rowA = i;
                        colA = j;
                    } else if (dt[i][j] != '.') {
                        letRow[size] = i;
                        letCol[size] = j;
                        size++;
                    }
                }
            }

            int[] toLeft = new int[n];
            int[] toRight = new int[n];
            for (int i = 0; i < n; i++) {
                int left = 0, pos = colA;
                while (pos >= 0 && (dt[i][pos] == '.' || dt[i][pos] == 'A')) {
                    left++;
                    pos--;
                }

                int right = 0;
                pos = colA;
                while (pos < m && (dt[i][pos] == '.' || dt[i][pos] == 'A')) {
                    right++;
                    pos++;
                }

                toLeft[i] = left;
                toRight[i] = right;
            }

            int rowBeg = -1, rowEnd = -1, sq = 0;
            int colBeg = -1, colEnd = -1;
            for (int i = 0; i <= rowA; i++) {
                int minL = Integer.MAX_VALUE, minR = Integer.MAX_VALUE;
                for (int k = i; k <= rowA; k++) {
                    minL = Math.min(minL, toLeft[k]);
                    minR = Math.min(minR, toRight[k]);
                }
                for (int j = rowA; j < n; j++) {
                    minL = Math.min(minL, toLeft[j]);
                    minR = Math.min(minR, toRight[j]);
                    int cur = (minL + minR - 1) * (j - i + 1);
                    if (cur > sq) {
                        sq = cur;
                        rowBeg = i;
                        rowEnd = j;
                        colBeg = colA - minL + 1;
                        colEnd = colA + minR - 1;
                    }
                }
            }

            for (int i = rowBeg; i <= rowEnd; i++) {
                for (int j = colBeg; j <= colEnd; j++) {
                    if (dt[i][j] == '.') {
                        dt[i][j] = 'a';
                    }
                }
            }

            int[] bordersFrom = new int[40];
            int[] bordersTo = new int[40];
            int am = 0;
            for (int t = 0; t < size; t++) {
                int row = letRow[t];
                char letter = Character.toLowerCase(dt[letRow[t]][letCol[t]]);
                row--;
                while (row >= 0 && dt[row][letCol[t]] == '.') {
                    dt[row][letCol[t]] = letter;
                    row--;
                }
                int from = row + 1;

                row = letRow[t] + 1;
                while (row < n && dt[row][letCol[t]] == '.') {
                    dt[row][letCol[t]] = letter;
                    row++;
                }
                int to = row - 1;

                bordersFrom[am] = from;
                bordersTo[am] = to;
                am++;
            }

            for (int l = 0; l < size; l++) {
                int row = letRow[l];
                int col = letCol[l];
                char letter = Character.toLowerCase(dt[row][col]);
                int from = bordersFrom[l];
                int to = bordersTo[l];

                col--;
                while (col >= 0) {
                    boolean ok = true;
                    for (int i = from; i <= to; i++) {
                        if (dt[i][col] != '.') {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        break;
                    }
                    for (int i = from; i <= to; i++) {
                        dt[i][col] = letter;
                    }
                    col--;
                }

                col = letCol[l] + 1;
                while (col < m) {
                    boolean ok = true;
                    for (int i = from; i <= to; i++) {
                        if (dt[i][col] != '.') {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        break;
                    }
                    for (int i = from; i <= to; i++) {
                        dt[i][col] = letter;
                    }
                    col++;
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    out.print(dt[i][j]);
                }
                out.println();
            }
            out.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static class FastScanner implements AutoCloseable {
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

        boolean isWord(char ch) {
            return ch == '.' || Character.isLetter(ch) || Character.isDigit(ch);
        }

        boolean isEOF() {
            return !EOF;
        }

        void skipSpaces() throws IOException {
            while (!isWord(ch) && isEOF()) {
                getNextSymbol();
            }
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
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

        @Override
        public void close() throws Exception {
            in.close();
        }
    }
}
