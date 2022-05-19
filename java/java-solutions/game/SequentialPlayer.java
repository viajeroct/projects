package game;

public class SequentialPlayer implements Player {
    @Override
    public Move makeMove(Position position) {
        for (int r = 0; r < position.getFieldSize().n; r++) {
            for (int c = 0; c < position.getFieldSize().m; c++) {
                final Move move = new Move(r, c, position.getTurn());
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        throw new AssertionError("No valid moves");
    }

    @Override
    public int DrawAnswer() {
        return 1;
    }
}
