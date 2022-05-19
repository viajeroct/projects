package game;

import java.util.Scanner;

public class HumanPlayer implements Player {
    private final Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    @Override
    public Move makeMove(Position position) {
        System.out.println("\n\n");
        System.out.println("<-$ Next choice $->");
        System.out.println("Enter you move for " + position.getTurn() +
                "\n\tIf you want to give up enter -1 -1" +
                "\n\tIf you want to suggest draw enter -2 -2");
        if (!in.hasNextInt()) {
            in.nextLine();
            return new Move(-4, -4, position.getTurn());
        }
        int x = in.nextInt() - 1;
        if (!in.hasNextInt()) {
            in.nextLine();
            return new Move(-4, -4, position.getTurn());
        }
        int y = in.nextInt() - 1;
        return new Move(x, y, position.getTurn());
    }

    @Override
    public int DrawAnswer() {
        return in.nextInt();
    }
}
