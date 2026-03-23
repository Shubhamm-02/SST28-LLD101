package models;

import java.util.*;

public class Board {
    private int size;
    private int boardSize;
    private List<Snake> snakes;
    private List<Ladder> ladders;
    private Map<Integer, Integer> snakeMap;   // head -> tail
    private Map<Integer, Integer> ladderMap;  // start -> end

    public Board(int size, String difficultyLevel) {
        this.size = size;
        this.boardSize = size * size;
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();
        this.snakeMap = new HashMap<>();
        this.ladderMap = new HashMap<>();
        initializeBoard(difficultyLevel);
    }

    private void initializeBoard(String difficultyLevel) {
        Random random = new Random();
        Set<Integer> occupiedPositions = new HashSet<>();
        // Position 1 and boardSize should never have a snake or ladder
        occupiedPositions.add(1);
        occupiedPositions.add(boardSize);

        // Place n snakes
        int count = 0;
        while (count < size) {
            int head, tail;
            if (difficultyLevel.equalsIgnoreCase("easy")) {
                // Easy: snakes mostly in the upper half
                head = randomInRange(random, boardSize / 2 + 1, boardSize - 1);
                tail = randomInRange(random, 2, head - 1);
            } else {
                // Hard: snakes anywhere
                head = randomInRange(random, 3, boardSize - 1);
                tail = randomInRange(random, 2, head - 1);
            }

            if (!occupiedPositions.contains(head) && !occupiedPositions.contains(tail)) {
                Snake snake = new Snake(head, tail);
                snakes.add(snake);
                snakeMap.put(head, tail);
                occupiedPositions.add(head);
                occupiedPositions.add(tail);
                count++;
            }
        }

        // Place n ladders
        count = 0;
        while (count < size) {
            int start, end;
            if (difficultyLevel.equalsIgnoreCase("easy")) {
                // Easy: ladders mostly in the lower half
                start = randomInRange(random, 2, boardSize / 2);
                end = randomInRange(random, start + 1, boardSize - 1);
            } else {
                // Hard: ladders anywhere
                start = randomInRange(random, 2, boardSize - 2);
                end = randomInRange(random, start + 1, boardSize - 1);
            }

            if (!occupiedPositions.contains(start) && !occupiedPositions.contains(end)) {
                Ladder ladder = new Ladder(start, end);
                ladders.add(ladder);
                ladderMap.put(start, end);
                occupiedPositions.add(start);
                occupiedPositions.add(end);
                count++;
            }
        }
    }

    private int randomInRange(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Returns the final position after checking for snakes or ladders.
     * Note: Only checks once — does not handle chaining.
     */
    public int getNewPosition(int position) {
        if (snakeMap.containsKey(position)) {
            int newPos = snakeMap.get(position);
            System.out.println("  🐍 Oops! Snake at " + position + " — sliding down to " + newPos);
            return newPos;
        }
        if (ladderMap.containsKey(position)) {
            int newPos = ladderMap.get(position);
            System.out.println("  🪜 Yay! Ladder at " + position + " — climbing up to " + newPos);
            return newPos;
        }
        return position;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public void printBoard() {
        System.out.println("\n========== BOARD SETUP ==========");
        System.out.println("Board: " + size + " x " + size + " (" + boardSize + " cells)");
        System.out.println("\nSnakes (" + snakes.size() + "):");
        for (Snake s : snakes) {
            System.out.println("  " + s);
        }
        System.out.println("\nLadders (" + ladders.size() + "):");
        for (Ladder l : ladders) {
            System.out.println("  " + l);
        }
        System.out.println("=================================\n");
    }
}
