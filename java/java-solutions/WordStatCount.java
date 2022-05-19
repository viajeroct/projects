import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class WordStatCount {
    public static void main(String[] args) {
        int res = 0;
        Ans[] ans = null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));

            try {
                Pair[] dt = new Pair[1];
                int count = 0;
                while (true) {
                    int ch = in.read();
                    if (ch == -1) {
                        break;
                    }
                    if (!isCorrect((char) ch)) {
                        continue;
                    }
                    StringBuilder word = new StringBuilder();
                    word.append((char) ch);
                    while (true) {
                        ch = in.read();
                        if (ch == -1 || !isCorrect((char) ch)) {
                            break;
                        }
                        word.append((char) (ch));
                    }
                    if (count == dt.length) {
                        dt = Arrays.copyOf(dt, dt.length * 2);
                    }
                    dt[count] = new Pair(word.toString().toLowerCase(), count);
                    count++;
                }

                Arrays.sort(dt, 0, count);
                String[] words = new String[count];
                int[] cnt = new int[count];
                int i = 0;
                int unique;
                while (i < count) {
                    unique = dt[i].position;
                    words[unique] = dt[i].word;
                    while (i < count && dt[i].word.equals(words[unique])) {
                        cnt[unique]++;
                        i++;
                    }
                }
                ans = new Ans[count];
                for (int j = 0; j < count; j++) {
                    if (words[j] != null) {
                        ans[res] = new Ans(words[j], cnt[j]);
                        res++;
                    }
                }

                Arrays.sort(ans, 0, res);
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Can't input: " + e.getMessage());
        }

        if (ans != null) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));

                try {
                    for (int j = 0; j < res; j++) {
                        out.write(ans[j].word + " " + ans[j].cnt);
                        out.newLine();
                    }
                } finally {
                    out.close();
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Can't output: " + e.getMessage());
            }
        }
    }

    static boolean isCorrect(char ch) {
        return Character.isLetter(ch) || ch == '\'' || Character.getType(ch) == Character.DASH_PUNCTUATION;
    }

    static class Ans implements Comparable<Ans> {
        String word;
        int cnt;

        Ans(String word, int cnt) {
            this.word = word;
            this.cnt = cnt;
        }

        @Override
        public int compareTo(Ans other) {
            return cnt - other.cnt;
        }
    }

    static class Pair implements Comparable<Pair> {
        String word;
        int position;

        Pair(String word, int position) {
            this.word = word;
            this.position = position;
        }

        @Override
        public int compareTo(Pair other) {
            return word.compareTo(other.word);
        }
    }
}
