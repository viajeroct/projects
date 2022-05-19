import java.util.Scanner;

public class TaskD {
    static final int mod = 998_244_353;

    static int mul(int x, int y) {
        return (int) (((long) x * y) % mod);
    }

    static int subst(int x, int y) {
        return (int) ((((long) x - y) % mod + mod) % mod);
    }

    static int pow(int a, int n) {
        int res = 1;
        while (n > 0) {
            if ((n & 1) == 1)
                res = mul(res, a);
            a = mul(a, a);
            n >>= 1;
        }
        return res;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(), k = in.nextInt();

        int[] r = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            if (i % 2 == 1) {
                r[i] = mul(pow(k, (i + 1) / 2), i);
            } else {
                r[i] = ((mul(pow(k, i / 2), (i / 2)) + mul(pow(k, i / 2 + 1), (i / 2))) % mod);
            }
        }

        int[] d = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            d[i] = r[i];
            for (int l = 1; l * l <= i; l++) {
                if (l == i) {
                    continue;
                }
                if (i % l == 0) {
                    d[i] = subst(d[i], mul(i / l, d[l]));
                    if (l != 1 && i / l != l) {
                        d[i] = subst(d[i], mul(l, d[i / l]));
                    }
                }
            }
        }

        int ans = 0;
        for (int i = 0; i <= n; i++) {
            for (int l = 1; l * l <= i; l++) {
                if (i % l == 0) {
                    ans = (ans + d[l]) % mod;
                    if (i / l != l) {
                        ans = (ans + d[i / l]) % mod;
                    }
                }
            }
        }

        System.out.println(ans);
    }
}
