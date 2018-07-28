
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Scanner input = new Scanner(System.in);
    private static char[][] state = new char[8][8];
    private static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private static String move, choice, a1, a2;
    private static char c1, c2;
    private static int x, currentPlayer, time, myTime;
    private static long startTime;
    private static Position p, position;
    private static Board board;
    private static Board currentState;

    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++)
                state[i][j] = '-';
        }
        System.out.println("Four in a line Game vs. AI");
        board = new Board(state);
        board.printBoard();

        System.out.print("Do you want to go first (y/n): ");
        choice = input.nextLine().toLowerCase();
        if (choice.equals("y"))
            currentPlayer = 1;
        else
            currentPlayer = 2;

        //PLAYER 1: HUMAN. PLAYER 2: MACHINE.

        System.out.print("What is the maximum allowed time for computer in seconds: ");
        time = input.nextInt() * 1000; //will terminate if search takes longer than allotted time

        while ((!board.checkWin('O')) && (!board.checkWin('X')) && (!board.checkDraw())) {

            //checks if board terminates, otherwise take first move

            if (currentPlayer == 1) {
                p = humanMove();
                while (!board.validMove(p)) {
                    System.out.println("Invalid move. Please try again.\n");
                    p = humanMove();
                }
                board.insertPiece(p, currentPlayer); //inserts human move
                board.printBoard(); //prints board
                if ((board.checkWin('O')) || (board.checkWin('X')) || (board.checkDraw())) { //terminate if draw/win/loss after human move
                    break;
                }
                currentPlayer = 2; //hand off to next player
            } else {
                aimove(10, time); //machine will use adversarial search
                currentPlayer = 1; //hand off to next player
            }
        }

        if (board.checkWin('O')) {
            System.out.println("You won! :)");
        } else if (board.checkWin('X')) {
            System.out.println("You lost :(");
        } else {
            System.out.println("It's a draw!");
        }
    }

    public static Position humanMove() {
        Position p = new Position(0, 0);
        boolean validMove = false;
        input = new Scanner(System.in);
        System.out.println("Enter your move: ");
        move = input.nextLine();
        c1 = move.charAt(0);
        c2 = move.charAt(1);
        x = Character.getNumericValue(c2);
        for (int i = 0; i < alphabet.length; i++) {
            if (alphabet[i] == c1) {
                validMove = true;
                break;
            }
        }
        while ((move.length() != 2) || (x < 1) || (x > 8) || (!validMove)) {

            //loop until valid move is found

            System.out.println("Invalid move. Re-enter your move: ");
            move = input.nextLine();
            c1 = move.charAt(0);
            c2 = move.charAt(1);
            x = Character.getNumericValue(c2);
            for (int i = 0; i < alphabet.length; i++) {
                if (alphabet[i] == c1) {
                    validMove = true;
                    break;
                }
            }
        }
        for (int i = 0; i < alphabet.length; i++) { //sets input into new position to return
            if (alphabet[i] == c1) {
                p.x = i;
                p.y = x - 1; //offset for index
            }
        }
        return p;
    }

    public static void aimove(int level, int time) {

        //adversarial search that determines best possible move
        int r, c;
        int count = 0, count1 = 0;
        int firstRowP = 0;
        int firstColumnP = 0;
        currentState = new Board(state);
        myTime = time;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < currentState.getBoard().length; i++) { //checks if board is empty, or the position of first move
            for (int j = 0; j < currentState.getBoard().length; j++) {
                if (currentState.getBoard()[i][j] == '-')
                    count++;
                if ((currentState.getBoard()[i][j] == 'O') || (currentState.getBoard()[i][j] == 'X')) {
                    count1++;
                    firstRowP = i;
                    firstColumnP = j;
                }
            }
        }
        if (count == 64) { //sets first piece if board is empty
            r = 3;
            c = 4;

        } else if (count1 == 1) { //designed to move to an adjacent spot from first move
            if (firstColumnP + 4 > 7) {
                r = firstRowP;
                c = firstColumnP - 1;
            } else {
                r = firstRowP;
                c = firstColumnP + 1;
            }
        } else {
            int[] result = pruning(level, currentPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
            r = result[1];
            c = result[2];
        }
        position = new Position(r, c);
        currentState.insertPiece(position, currentPlayer);
        currentState.printBoard();
        a1 = Character.toString(alphabet[r]);
        a2 = Integer.toString(c + 1);
        System.out.println("AI move: " + a1 + a2);
    }

    private static int[] pruning(int depth, int player, int alpha, int beta) {

        //alpha-beta pruning used during adversarial search

        int score;
        int bestRow = -1;
        int bestColumn = -1;

        ArrayList<Position> neighbors = currentState.findNeighbors();

        if (neighbors.size() == 1) {
            score = currentState.eval();
            return new int[]{score, neighbors.get(0).x, neighbors.get(0).y};
        } else if (neighbors.isEmpty() || depth == 0 || (System.currentTimeMillis() - startTime >= myTime)) {
            //will terminate at current state if time has run out, otherwise keep checking scores for best move
            score = currentState.eval();
            return new int[]{score, bestRow, bestColumn};
        } else {
            for (Position point : neighbors) {
                if (player == 2) {
                    currentState.insertPiece(point, player);
                    score = pruning(depth - 1, 1, alpha, beta)[0];
                    if (score > alpha) {
                        alpha = score;
                        bestRow = point.x;
                        bestColumn = point.y;
                    }
                } else {
                    currentState.insertPiece(point, player);
                    score = pruning(depth - 1, 2, alpha, beta)[0];
                    if (score < beta) {
                        beta = score;
                        bestRow = point.x;
                        bestColumn = point.y;
                    }
                }
                currentState.undoPiece(point);
                if (alpha >= beta) break; //prune happens here
            }

            //return either the min move or the max move depending on which player is active
            return new int[]{(player == 2) ? alpha : beta, bestRow, bestColumn};
        }
    }
}
