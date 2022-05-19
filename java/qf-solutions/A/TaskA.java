import java.util.Scanner;

public class TaskA {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        long a = in.nextLong(), b = in.nextLong(), n = in.nextLong();
        System.out.println(2 * ((n - a - 1) / (b - a)) + 1);
    }
}
