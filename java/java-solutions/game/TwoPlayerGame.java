package game;

public class TwoPlayerGame {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private final int[] cntDraws;

    public TwoPlayerGame(Board board, Player player1, Player player2) {
        this.board = board;
        System.out.println("Current state of board: ");
        System.out.println(board);
        this.player1 = player1;
        this.player2 = player2;
        cntDraws = new int[2];
    }

    public int askPlayer(Player player, int no, boolean log, Player another) {
        int result = makeMove(player, no, log);
        while (result == 3) {
            System.out.println("Sorry, but your choice was wrong...");
            result = makeMove(player, no, log);
        }
        if (result == 4) {
            System.out.println("Player number " + no + " suggested draw");
            cntDraws[no - 1]++;
            if (cntDraws[no - 1] >= 2) {
                System.out.println("Suggested to many draws...");
                return 3 - no;
            }
            System.out.println("If you accept then enter 1 else 2");
            int cmd = another.DrawAnswer();
            if (cmd == 1) {
                return 0;
            } else {
                System.out.println("Draw was declined! Enter again!");
                return askPlayer(player, no, log, another);
            }
        }
        if (result == 5) {
            System.out.println("Enter again...");
            return askPlayer(player, no, log, another);
        }
        return result;
    }

    public int play(boolean log) {
        while (true) {
            int result1 = askPlayer(player1, 1, log, player2);
            if (result1 != -1) {
                return result1;
            }
            int result2 = askPlayer(player2, 2, log, player2);
            if (result2 != -1) {
                return result2;
            }
        }
    }

    private int makeMove(Player player, int no, boolean log) {
        final Move move = player.makeMove(board.getPosition());
        if (move.getRow() == -4 && move.getCol() == -4) {
            return 5;
        }
        final GameResult result = board.makeMove(move);
        if (log) {
            System.out.println("Player: " + no);
            System.out.println(move);
            System.out.println("Current state of board");
            System.out.println(board);
            System.out.println("Current state of game: " + result);
        }
        return switch (result) {
            case WIN -> no;
            case DRAW -> 0;
            case UNKNOWN -> -1;
            case WRONG_CHOICE -> 3;
            case GIVE_UP -> 3 - no;
            case SUGGEST_DRAW -> 4;
            case WRONG_INPUT -> 5;
        };
    }
}
