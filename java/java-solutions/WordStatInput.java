import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class WordStatInput {
    public static void main(String[] args) {
        String[] words = null;
        int[] cnt = null;
        int count = 0;

        try {
            FastScannerWordStat in = new FastScannerWordStat(args[0], StandardCharsets.UTF_8);

            try {
                Pair[] dt = new Pair[1];
                for (String word = in.next(); !in.isEOF(); word = in.next()) {
                    if (count == dt.length) {
                        dt = Arrays.copyOf(dt, dt.length * 2);
                    }
                    if (word == null) {
                        continue;
                    }
                    dt[count] = new Pair(word.toLowerCase(), count);
                    count++;
                }
                Arrays.sort(dt, 0, count);
                words = new String[count];
                cnt = new int[count];
                int i = 0, unique;
                while (i < count) {
                    unique = dt[i].position;
                    words[unique] = dt[i].word;
                    while (i < count && dt[i].word.equals(words[unique])) {
                        cnt[unique]++;
                        i++;
                    }
                }
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Can't input: " + e.getMessage());
        }

        if (words != null) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));

                try {
                    int res = 0;
                    for (int j = 0; j < count; j++)
                        if (words[j] != null) {
                            out.write(words[j] + " " + cnt[j]);
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
