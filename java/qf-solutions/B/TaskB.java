import java.util.Scanner;

public class TaskB {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int n = in.nextInt();
        int x = -710 * 25_000;
        int shift = 710;
        for (int i = 0; i < n; i++, x += shift) {
            System.out.println(x);
        }
    }
}
