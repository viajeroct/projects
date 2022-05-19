public class Sum {
    public static void main(String[] args) {
        int res = 0;
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args[i].length(); j++) {
                if (Character.isWhitespace(args[i].charAt(j))) {
                    continue;
                }
                int cur = j;
                while (cur < args[i].length() && !Character.isWhitespace(args[i].charAt(cur))) {
                    cur++;
                }
                String s = args[i].substring(j, cur);
                if (s.equals("+")) {
                    continue;
                }
                res += Integer.parseInt(s);
                j = cur - 1;
            }
        }
        System.out.println(res);
    }
}
