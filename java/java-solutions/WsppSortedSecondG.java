import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class WsppSortedSecondG {
    public static void main(String[] args) {
        TreeMap<String, Pair> cnt = null;

        try {
            FastScannerWordStat sc = new FastScannerWordStat(args[0], StandardCharsets.UTF_8);

            try {
                cnt = new TreeMap<>();
                int numeration = 1;
                int stringNumber = 1;
                while (!sc.isEOF()) {
                    String word = sc.next();
                    if (sc.isNextLine()) {
                        stringNumber++;
                        continue;
                    }
                    word = word.toLowerCase();
                    if (!cnt.containsKey(word)) {
                        cnt.put(word, new Pair(stringNumber));
                    }
                    Pair current = cnt.get(word);
                    int it_str = current.getStringNumber();
                    if (it_str != stringNumber) {
                        current.setIndex(1);
                        current.setStr(stringNumber);
                    }
                    if (current.getIndex() % 2 == 0) {
                        current.add(numeration);
                    }
                    current.incrIndex();
                    current.incrTotal();
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
                    for (Map.Entry<String, Pair> it : cnt.entrySet()) {
                        IntList current = it.getValue().getDt();
                        out.write(it.getKey() + " " + it.getValue().getTotal());
                        for (int i = 0; i < current.size(); i++) {
                            int val = current.get(i);
                            out.write(" " + val);
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
