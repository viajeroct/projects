import java.util.Scanner;

public class TaskI {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        long xl = Long.MAX_VALUE, xr = Long.MIN_VALUE;
        long yl = Long.MAX_VALUE, yr = Long.MIN_VALUE;

        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            long xi = in.nextLong(), yi = in.nextLong(), hi = in.nextLong();
            xl = Math.min(xl, xi - hi);
            yl = Math.min(yl, yi - hi);
            xr = Math.max(xr, xi + hi);
            yr = Math.max(yr, yi + hi);
        }

        long h = (Math.max(xr - xl, yr - yl) + 1) / 2;
        System.out.printf("%d %d %d\n", (xl + xr) / 2, (yl + yr) / 2, h);
    }
}
