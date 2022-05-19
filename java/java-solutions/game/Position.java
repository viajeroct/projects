package game;

public class Position {
    private final int n, m;
    private final Cell[][] link;
    private Cell turn;

    public Position(int n, int m, Cell turn, Cell[][] link) {
        this.n = n;
        this.m = m;
        this.turn = turn;
        this.link = link;
    }

    public Cell getTurn() {
        return turn;
    }

    public void setTurn(Cell turn) {
        this.turn = turn;
    }

    public Size getFieldSize() {
        return new Size(n, m);
    }

    public boolean isGiveUp(Move move) {
        return move.getRow() == -2 && move.getCol() == -2;
    }

    public boolean isSuggestedDraw(Move move) {
        return move.getRow() == -3 && move.getCol() == -3;
    }

    public boolean isValid(Move move) {
        return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getCol() && move.getCol() < m
                && link[move.getRow()][move.getCol()] == Cell.E
                && turn == move.getValue();
    }
}
