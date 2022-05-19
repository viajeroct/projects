package game;

import java.util.Arrays;
import java.util.Map;

public class TicTacToeBoard implements Board {
    static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0"
    );

    final Cell[][] field;
    final int n;
    final int m;
    final int k;
    final int rowFormat, colFormat;
    final Position position;
    int totalField;

    public TicTacToeBoard(int n, int m, int k) {
        field = new Cell[n][m];
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }
        this.n = n;
        rowFormat = Integer.toString(n).length();
        this.m = m;
        colFormat = Integer.toString(m).length();
        this.k = k;
        totalField = 0;
        position = new Position(n, m, Cell.X, field);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public GameResult makeMove(Move move) {
        if (position.isGiveUp(move)) {
            return GameResult.GIVE_UP;
        }
        if (position.isSuggestedDraw(move)) {
            return GameResult.SUGGEST_DRAW;
        }
        if (!position.isValid(move)) {
            return GameResult.WRONG_CHOICE;
        }

        field[move.getRow()][move.getCol()] = move.getValue();
        totalField++;
        if (checkWin(move.getRow(), move.getCol())) {
            return GameResult.WIN;
        }

        if (totalField == n * m) {
            return GameResult.DRAW;
        }

        position.setTurn(position.getTurn() == Cell.X ? Cell.O : Cell.X);
        return GameResult.UNKNOWN;
    }

    public int count(int row, int col, int dirRow, int dirCol) {
        Cell symbol = field[row][col];
        int cnt = 0;
        do {
            if (symbol == field[row][col]) {
                cnt++;
            } else {
                break;
            }
            row += dirRow;
            col += dirCol;
        } while (0 <= row && row < n && 0 <= col && col < m && cnt <= k);
        return cnt;
    }

    protected boolean checkWin(int row, int col) {
        int first = count(row, col, -1, 0) + count(row, col, 1, 0) - 1;
        int second = count(row, col, 0, -1) + count(row, col, 0, 1) - 1;
        int third = count(row, col, -1, -1) + count(row, col, 1, 1) - 1;
        int fourth = count(row, col, -1, 1) + count(row, col, 1, -1) - 1;
        return first >= k || second >= k || third >= k || fourth >= k;
    }

    @Override
    public String toString() {
        StringBuilder generate = new StringBuilder().append(" ".repeat(rowFormat + 1));
        for (int i = 1; i <= m; i++) {
            generate.append(String.format("%" + (colFormat) + "d ", i));
        }
        final StringBuilder sb = new StringBuilder(generate).append(System.lineSeparator());
        for (int r = 0; r < n; r++) {
            sb.append(String.format("%" + rowFormat + "d ", r + 1));
            for (Cell cell : field[r]) {
                sb.append(String.format("%" + colFormat + "s ", CELL_TO_STRING.get(cell)));
            }
            sb.append(System.lineSeparator());
        }
        sb.setLength(sb.length() - System.lineSeparator().length());
        return sb.toString();
    }
}
