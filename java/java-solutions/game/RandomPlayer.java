package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random = new Random();

    @Override
    public Move makeMove(Position position) {
        while (true) {
            final Move move = new Move(
                    random.nextInt(position.getFieldSize().n),
                    random.nextInt(position.getFieldSize().m),
                    position.getTurn()
            );
            if (position.isValid(move)) {
                return move;
            }
        }
    }

    @Override
    public int DrawAnswer() {
        return random.nextInt() % 2 + 1;
    }
}
