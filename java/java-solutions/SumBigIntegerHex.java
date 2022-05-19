import java.math.BigInteger;

public class SumBigIntegerHex {
    public static void main(String[] args) {
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args[i].length(); j++) {
                if (Character.isWhitespace(args[i].charAt(j))) {
                    continue;
                }
                int begin = j;
                while (j < args[i].length() && !Character.isWhitespace(args[i].charAt(j))) {
                    j++;
                }
                String s = args[i].substring(begin, j);
               	int radix = 10;
               	if (s.length() > 2 && s.toLowerCase().startsWith("0x")) {
               	    radix = 16;
               	    s = s.substring(2);
               	}
                res = res.add(new BigInteger(s, radix));
            }
        }
        System.out.println(res);
    }
}
