import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wspp {
    public static void main(String[] args) {
        LinkedHashMap<String, IntList> cnt = null;

        try {
            FastScannerWordStat sc = new FastScannerWordStat(args[0], StandardCharsets.UTF_8);

            try {
                int numeration = 1;
                cnt = new LinkedHashMap<>();
                while (!sc.isEOF()) {
                    String word = sc.next();
                    if (word == null || word.strip().isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    if (!cnt.containsKey(word)) {
                        cnt.put(word, new IntList());
                    }
                    cnt.get(word).add(numeration);
                    numeration++;
                }
            } finally {
                sc.close();
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Input file %s not found: " + e.getMessage(), args[0]);
        } catch (IOException e) {
            System.out.printf("Can't input from file %s: " + e.getMessage(), args[0]);
        }

        if (cnt != null) {
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8)
                );

                try {
                    for (Map.Entry<String, IntList> it : cnt.entrySet()) {
                        IntList current = it.getValue();
                        out.write(it.getKey() + " " + current.size());
                        for (int i = 0; i < current.size(); i++) {
                            out.write(" " + current.get(i));
                        }
                        out.newLine();
                    }
                } finally {
                    out.close();
                }
            } catch (FileNotFoundException e) {
                System.out.printf("Output file %s not found: " + e.getMessage(), args[1]);
            } catch (IOException e) {
                System.out.printf("Can't output to file %s: " + e.getMessage(), args[1]);
            }
        }
    }
}
