package service;

import models.*;

import java.util.List;

public class GameService {
    private Board board;
    private List<Player> players;
    private Dice dice;
    private int totalPlayers;
    private int playersFinished;

    public GameService(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.dice = new Dice();
        this.totalPlayers = players.size();
        this.playersFinished = 0;
    }

    public void startGame() {
        System.out.println("🎮 Game Started!\n");
        int currentPlayerIndex = 0;

        while (playersStillPlaying() > 1) {
            Player currentPlayer = players.get(currentPlayerIndex);

            if (currentPlayer.hasWon()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % totalPlayers;
                continue;
            }

            int diceValue = dice.roll();
            int currentPosition = currentPlayer.getPosition();
            int newPosition = currentPosition + diceValue;

            System.out.println(currentPlayer.getName() + " rolled a " + diceValue
                    + " | Current position: " + currentPosition);

            if (newPosition > board.getBoardSize()) {
                System.out.println("  Cannot move — would go beyond the board. Stay at " + currentPosition + ".");
            } else if (newPosition == board.getBoardSize()) {
                currentPlayer.setPosition(newPosition);
                currentPlayer.setHasWon(true);
                playersFinished++;
                int rank = playersFinished;
                System.out.println("  🏆 " + currentPlayer.getName() + " reached position "
                        + board.getBoardSize() + " and WINS! (Rank #" + rank + ")");
            } else {
                // Check for snakes or ladders
                int finalPosition = board.getNewPosition(newPosition);
                currentPlayer.setPosition(finalPosition);
                System.out.println("  Moved to position " + finalPosition);
            }

            System.out.println();
            currentPlayerIndex = (currentPlayerIndex + 1) % totalPlayers;
        }

        // Announce the last remaining player
        for (Player p : players) {
            if (!p.hasWon()) {
                System.out.println("🏁 Game Over! " + p.getName() + " finishes last at position " + p.getPosition() + ".");
            }
        }
    }

    private int playersStillPlaying() {
        int count = 0;
        for (Player p : players) {
            if (!p.hasWon()) {
                count++;
            }
        }
        return count;
    }
}
