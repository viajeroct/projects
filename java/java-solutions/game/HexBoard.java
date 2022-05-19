package game;

public class HexBoard extends TicTacToeBoard {
    public HexBoard(int n, int k) {
        super(n, n, k);
    }

    @Override
    protected boolean checkWin(int row, int col) {
        int first = count(row, col, -1, 0) + count(row, col, 1, 0) - 1;
        int second = count(row, col, 0, -1) + count(row, col, 0, 1) - 1;
        int third = count(row, col, -1, -1) + count(row, col, 1, 1) - 1;
        return first >= k || second >= k || third >= k;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int size = 1, diff = 1, offset = n - 1, index = 1, sum = 0;
        System.out.print(" ".repeat(offset + 2));
        System.out.println(index + " " + index++);
        while (true) {
            if (size == n + 1) {
                diff *= -1;
                size = n - 1;
                offset = 1;
            }
            if (size == 0) {
                break;
            }
            sb.append(" ".repeat(offset));
            if (index <= n) {
                sb.append(String.format("%2d ", index));
            } else {
                sb.append("   ");
            }
            for (int i = 0; i < n; i++) {
                if (sum - i >= 0 && sum - i < n) {
                    sb.append(CELL_TO_STRING.get(field[i][sum - i])).append(" ");
                }
            }
            if (index <= n) {
                sb.append(String.format("%d", index++));
            }
            sb.append(System.lineSeparator());
            size += diff;
            offset += diff * (-1);
            sum += 1;
        }
        sb.setLength(sb.length() - System.lineSeparator().length());
        return sb.toString();
    }
}
