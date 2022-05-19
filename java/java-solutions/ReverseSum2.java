import java.util.Arrays;
import java.util.Scanner;

public class ReverseSum2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int[] sumcols = new int[1];
        while (in.hasNextLine()) {
            Scanner line = new Scanner(in.nextLine());
            int sum = 0;
            for (int i = 0; line.hasNextInt(); i++) {
                int num = line.nextInt();
                if (i >= sumcols.length) {
                    sumcols = Arrays.copyOf(sumcols, sumcols.length * 2);
                }
                sumcols[i] += num;
                sum += sumcols[i];
                System.out.print(sum + " ");
            }
            System.out.println();
        }
    }
}
