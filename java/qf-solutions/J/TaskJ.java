import java.util.Scanner;

public class TaskJ {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int n = in.nextInt();
        int[][] dt = new int[n][n];
        for (int i = 0; i < n; i++) {
            String cur = in.next();
            for (int j = 0; j < n; j++) {
                dt[i][j] = cur.charAt(j) - '0';
            }
        }

        int[][] ans = new int[n][n];
        for (int v = 0; v < n; v++) {
            for (int u = v + 1; u < n; u++) {
                if (dt[v][u] == 0) {
                    continue;
                }
                ans[v][u] = 1;
                for (int k = u + 1; k < n; k++) {
                    dt[v][k] = (dt[v][k] - dt[u][k]) % 10;
                    dt[v][k] = (dt[v][k] + 10) % 10;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(ans[i][j]);
            }
            System.out.println();
        }
    }
}
