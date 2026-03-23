import models.*;
import service.GameService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter board size (n for n x n board): ");
        int n = scanner.nextInt();

        System.out.print("Enter number of players: ");
        int x = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter difficulty level (easy/hard): ");
        String difficultyLevel = scanner.nextLine().trim();

        if (x < 2) {
            System.out.println("Need at least 2 players to play!");
            return;
        }

        if (!difficultyLevel.equalsIgnoreCase("easy") && !difficultyLevel.equalsIgnoreCase("hard")) {
            System.out.println("Invalid difficulty level. Choose 'easy' or 'hard'.");
            return;
        }

        // Create the board
        Board board = new Board(n, difficultyLevel);
        board.printBoard();

        // Create players
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= x; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = scanner.nextLine().trim();
            players.add(new Player(name));
        }

        System.out.println();

        // Start the game
        GameService gameService = new GameService(board, players);
        gameService.startGame();

        scanner.close();
    }
}
