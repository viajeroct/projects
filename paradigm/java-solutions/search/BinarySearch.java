package search;

public class BinarySearch {
    // Pred: arr.length > 0 && min(arr[i]) <= x
    // Post: min i : arr[i] <= x
    private static int IterativeBinarySearch(final int x, final int[] arr) {
        // P: Pred of IterativeBinarySearch
        int l = -1, r = arr.length - 1;
        // Q: l == -1 && r == arr.length - 1
        // P: arr[r] <= x && (l == -1 || arr[l] > x) && min i : arr[i] <= x; i in (l; r]
        while (r - l > 1) {
            // P: r - l > 1
            final int m = (l + r) / 2;
            // Q: m == (l + r) / 2
            // P: m == (l + r) / 2
            if (arr[m] <= x) {
                // P: arr[m] <= x
                r = m;
                // Q: r == m && arr[r] <= x
            } else {
                // P: arr[m] > x
                l = m;
                // Q: l == m && arr[l] > x
            }
            // Q: arr[r] <= x && (l == -1 || arr[l] > x) && min i : arr[i] <= x; i in (l; r]
        }
        // Q: min i : arr[i] <= x; r == i && r - l <= 1 && arr[r] <= x && (l == -1 || arr[l] > x)
        // P: min i : arr[i] <= x; r == i
        return r;
        // Q: min i : arr[i] <= x; r == i
    }

    // Pred: from == 0 && to == arr.length - 1 && arr.length > 0 && min(arr[i]) <= x
    // Post: min i : arr[i] <= x
    private static int RecursiveBinarySearch(final int x, final int[] arr, final int from, final int to) {
        // P: min i : arr[i] <= x exists && i in [from; to] && from >= 0 &&
        //    to <= arr.length - 1
        final int m = (from + to) / 2;
        // Q: m == (from + to) // 2
        // P: m == (from + to) // 2
        if (from >= to) {
            // P: from >= to
            return from;
            // Q: from >= to
        } else if (arr[m] <= x) {
            // P: from < to && arr[m] <= x && ans in left part
            return RecursiveBinarySearch(x, arr, from, m);
            // Q: from < to && arr[m] <= x
        } else {
            // P: from < to && arr[m] > x && ans in right part
            return RecursiveBinarySearch(x, arr, m + 1, to);
            // Q: from < to && arr[m] > x
        }
        // Q: Post of RecursiveBinarySearch
    }

    // Pred: args.length > 0 && args[i] - Integer
    // Post:
    //     1. min(arr[i]) > x => arr.length
    //     2. arr.length == 0 => 0
    //     3. min i : arr[i] <= x
    public static void main(final String[] args) {
        // P: args.length > 0
        final int x = Integer.parseInt(args[0]);
        // Q: x >= 0
        // P: args.length > 0
        final int[] arr = new int[args.length - 1];
        // Q: arr.length == args.length - 1
        // P: args.length > 0 && arr.length == args.length - 1
        for (int i = 0; i < args.length - 1; i++) {
            // P: args[i + 1] - Integer
            arr[i] = Integer.parseInt(args[i + 1]);
            // Q: arr[i] == args[i + 1]
        }
        // Q: i >= args.length - 1 && arr[i] - Integer
        // P: arr.length >= 0
        if (arr.length == 0) {
            // P: arr.length == 0
            System.out.println(0);
            // Q: arr.length == 0
        } else if (arr[arr.length - 1] > x) {
            // P: arr.length > 0 && min(arr) > x <=> arr[arr.length - 1] > x
            System.out.println(arr.length);
            // Q: arr.length > 0 && min(arr) > x <=> arr[arr.length - 1] > x
        } else {
            // P: arr.length > 0 && exists i : a[i] <= x
            // System.out.println(RecursiveBinarySearch(x, arr, 0, arr.length - 1));
            // Q: min i : arr[i] <= x

            // P: arr.length > 0 && exists i : a[i] <= x
            System.out.println(IterativeBinarySearch(x, arr));
            // Q: min i : arr[i] <= x
        }
        // Q: Post of main
    }
}
 