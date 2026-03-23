package models;

import java.util.Random;

public class Dice {
    private Random random;

    public Dice() {
        this.random = new Random();
    }

    public int roll() {
        // Returns a random value for a six-sided dice
        return random.nextInt(6);
    }
}
