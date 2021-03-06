package game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int result = new TwoPlayerGame(
                new TicTacToeBoard(8,18, 3),
                new HumanPlayer(new Scanner(System.in)),
                new HumanPlayer(new Scanner(System.in))
        ).play(true);
        switch (result) {
            case 1 -> System.out.println("First player won");
            case 2 -> System.out.println("Second player won");
            case 0 -> System.out.println("Draw");
            default -> throw new AssertionError("Unknown result " + result);
        }
    }
}
