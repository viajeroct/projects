import java.util.HashMap;
import java.util.Scanner;

public class TaskM {
    private final static Scanner in = new Scanner(System.in);

    private static long solveTest() {
        int n = in.nextInt();
        int[] dt = new int[n];
        for (int i = 0; i < n; i++) {
            dt[i] = in.nextInt();
        }

        long result = 0;
        HashMap<Integer, Integer> cnt = new HashMap<>();
        for (int j = n - 1; j >= 1; j--) {
            for (int i = 0; i <= j - 1; i++) {
                result += cnt.getOrDefault(2 * dt[j] - dt[i], 0);
            }
            cnt.put(dt[j], cnt.getOrDefault(dt[j], 0) + 1);
        }

        return result;
    }

    public static void main(String[] args) {
        int tests = in.nextInt();
        while (tests > 0) {
            System.out.println(solveTest());
            tests--;
        }
    }
}
