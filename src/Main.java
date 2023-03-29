import java.util.Scanner;

public class Main {
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private static final int BOARD_SIZE = 3;
    private static char aiSymbol;
    private static final int INITIAL_VALUE = 0;
    private static char[][] board;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Ask for the player's name
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();

        char[] symbols = choosePlayerSymbol(scanner);
        char playerSymbol = symbols[0];
        aiSymbol = symbols[1];

        // Choosing AI difficulty
        boolean isAiIntelligent = chooseAiDifficulty(scanner);

        // Choosing who goes first
        boolean isPlayerTurn = chooseWhoGoesFirst(scanner);

        initializeBoard();

        // Main game
        boolean isGameOver = false;
        char winner = ' ';

        while (!isGameOver && !checkForDraw()) {
            printBoard();

            if (isPlayerTurn) {
                System.out.println(playerName + ", it's your turn.");
                getPlayerMove(scanner, playerSymbol);
            } else {
                if (isAiIntelligent) {
                    System.out.println("The computer is thinking...");
                    aiMoveIntelligent(aiSymbol, playerSymbol);
                } else {
                    System.out.println("The computer is making a move...");
                    aiMoveWeak();
                }
            }

            // Checking if the game is over
            winner = checkForWinner();
            if (winner != ' ') {
                isGameOver = true;
                break;
            } else if (checkForDraw()) {
                printBoard();
                System.out.println("It's a draw!");
                break;
            }

            isPlayerTurn = !isPlayerTurn;
        }

        printBoard();

        if (winner == playerSymbol) {
            System.out.println("Congratulations, " + playerName + ", you won!");
        } else if (winner == aiSymbol) {
            System.out.println("Sorry, " + playerName + ", you lost.");
        }

        if (askToRestart(scanner)) {
            main(args);
        } else {
            System.out.println("Thanks for playing!");
        }
    }

    private static void initializeBoard() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = ' ';
            }
        }
    }

    private static void printBoard() {
        System.out.println("   1   2   3");
        System.out.println("  +---+---+---+");
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < BOARD_SIZE; col++) {
                System.out.print("| " + board[row][col] + " ");
            }
            System.out.println("|");
            System.out.println("  +---+---+---+");
        }
    }


    private static char[] choosePlayerSymbol(Scanner scanner) {
        while (true) {
            System.out.print("Choose your symbol (X or O): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("X")) {
                return new char[]{PLAYER_X, PLAYER_O};
            } else if (input.equalsIgnoreCase("O")) {
                return new char[]{PLAYER_O, PLAYER_X};
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    private static boolean chooseAiDifficulty(Scanner scanner) {
        while (true) {
            System.out.print("Choose the AI difficulty (W for weak, I for intelligent): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("W")) {
                return false;
            } else if (input.equalsIgnoreCase("I")) {
                return true;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    private static boolean chooseWhoGoesFirst(Scanner scanner) {
        while (true) {
            System.out.print("Who goes first, you or the computer? (P/C): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("P")) {
                return true;
            } else if (input.equalsIgnoreCase("C")) {
                return false;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    private static void getPlayerMove(Scanner scanner, char symbol) {
        while (true) {
            System.out.print("Enter your move (row col): ");
            String[] input = scanner.nextLine().split(" ");
            if (input.length != 2) {
                System.out.println("Invalid input, please try again.");
                continue;
            }
            int row = Integer.parseInt(input[0]) - 1;
            int col = Integer.parseInt(input[1]) - 1;
            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                System.out.println("Invalid input, please try again.");
                continue;
            }
            if (board[row][col] != ' ') {
                System.out.println("That spot is already taken, please try again.");
                continue;
            }
            board[row][col] = symbol;
            break;
        }
    }

    private static void aiMoveWeak() {
        int row, col;
        do {
            row = (int) (Math.random() * BOARD_SIZE);
            col = (int) (Math.random() * BOARD_SIZE);
        } while (board[row][col] != ' ');
        board[row][col] = aiSymbol;
    }

    private static void aiMoveIntelligent(char aiSymbol, char playerSymbol) {
        int[] move = minimax(playerSymbol, aiSymbol, playerSymbol);
        board[move[0]][move[1]] = aiSymbol;

    }

    private static int[] minimax(char symbol, char aiSymbol, char playerSymbol) {
        int bestScore = (symbol == aiSymbol) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    board[row][col] = symbol;
                    int score = minimaxHelper(symbol == playerSymbol ? aiSymbol : playerSymbol, aiSymbol, playerSymbol);
                    board[row][col] = ' ';
                    if ((symbol == aiSymbol && score > bestScore) || (symbol == playerSymbol && score < bestScore)) {
                        bestScore = score;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }
        return bestMove;
    }
    private static int minimaxHelper(char symbol, char aiSymbol, char playerSymbol) {
        char winner = checkForWinner();
        if (winner != ' ') {
            return (winner == aiSymbol) ? 1 : -1;
        } else if (checkForDraw()) {
            return 0;
        }

        int bestScore = (symbol == PLAYER_O) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    board[row][col] = symbol;
                    int score = minimaxHelper((symbol == PLAYER_O) ? PLAYER_X : PLAYER_O, aiSymbol, playerSymbol);
                    board[row][col] = ' ';
                    if ((symbol == PLAYER_O && score > bestScore) || (symbol == PLAYER_X && score < bestScore)) {
                        bestScore = score;
                    }
                }
            }
        }

        return bestScore;
    }

    private static boolean checkForDraw() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private static char checkForWinner() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2] && board[row][0] != ' ') {
                return board[row][0];
            }
        }
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col] && board[0][col] != ' ') {
                return board[0][col];
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ') {
            return board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ') {
            return board[0][2];
        }

        return ' ';

    }
    private static boolean askToRestart(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to restart the game? (Y/N): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            } else if (input.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }
}
