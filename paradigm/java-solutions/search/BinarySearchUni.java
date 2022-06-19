package search;

public class BinarySearchUni {
    // Pred: arr.length >= 2 && arr[i] - Integer &&
    //       args = arr1 ++ arr2, arr1 - decreasing, arr2 - increasing
    // Post: min possible len of arr1
    public static int UniIter(int[] arr) {
        // P: arr.length >= 2
        int l = -1, r = arr.length - 1;
        // Q: l == -1 && r == arr.length - 1
        // P: l == -1 && r == arr.length - 1 && arr.length >= 2 &&
        //    min(arr1) >= l && min(arr1) <= r
        while (r - l > 1) {
            // P: r - l > 1 && min(arr1) >= l && min(arr1) <= r
            int m = (l + r) / 2;
            // Q: m == (l + r) / 2
            // P: m >= 0 && m + 1 < arr.length
            int delta = arr[m + 1] - arr[m];
            // Q: delta == arr[m + 1] - arr[m]
            // P: delta - Integer
            if (delta <= 0) {
                // P: delta < 0
                l = m;
                // Q: l' == m && l == l'
            } else {
                // P: delta >= 0
                r = m;
                // Q: r' == m && r == r'
            }
            // Q: min(arr1) >= l' && min(arr1) <= r'
        }
        // Q: r - l <= 1 && min(arr1) >= l && min(arr1) <= r
        return l + 1;
        // Q: l + 1 = min(len(arr1)) R == l + 1
    }

    // Pred: arr.length >= 2 && arr[i] - Integer && l == -1 && r == arr.length - 1 &&
    //       args = arr1 ++ arr2, arr1 - decreasing, arr2 - increasing
    // Post: min len(arr1)
    public static int UniRec(int[] arr, int l, int r) {
        // P: min(arr1) >= l && min(arr1) <= r
        if (r - l <= 1) {
            // P: r - l <= 1
            return l + 1;
            // Q: R == l + 1
        }
        // Q: r - l > 1
        // P: r - l > 1
        int m = (l + r) / 2;
        // Q: m == (l + r) / 2
        // P: m >= 0 && m + 1 < arr.length
        int delta = arr[m + 1] - arr[m];
        // Q: delta == arr[m + 1] - arr[m]
        // P: delta - Integer
        if (delta <= 0) {
            // P: delta < 0
            return UniRec(arr, m, r);
            // Q: R = min(len(arr1))
        } else {
            // P: delta >= 0
            return UniRec(arr, l, m);
            // Q: R = min(len(arr1))
        }
    }

    // Pred:
    //     1. args.length > 0 && args[i] - Integer
    //     2. args = arr1 ++ arr2, arr1 - decreasing, arr2 - increasing
    // Post:
    //     min possible len of arr1
    public static void main(String[] args) {
        // P: args.length > 0
        int[] arr = new int[args.length];
        // Q: arr.length == args.length
        // P: args.length > 0 && arr.length == args.length
        for (int i = 0; i < args.length; i++) {
            // P: args[i] - Integer
            arr[i] = Integer.parseInt(args[i]);
            // Q: arr[i] == args[i]
        }
        // Q: i >= args.length && arr[i] - Integer &&
        //    args = arr1 ++ arr2, arr1 - decreasing, arr2 - increasing
        // P: arr.length >= 1
        if (arr.length == 1) {
            // P: arr.length == 1
            System.out.println(0);
            // Q: R == 0
        } else {
            // P: arr.length != 1 && l == -1 && r == arr.length - 1
            // System.out.println(UniRec(arr, -1, arr.length - 1));
            // Q: R = min possible len of arr1

            // P: arr.length != 1
            System.out.println(UniIter(arr));
            // Q: R = min possible len of arr1
        }
    }
}
