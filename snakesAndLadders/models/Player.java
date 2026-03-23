package models;

public class Player {
    private String name;
    private int position;
    private boolean hasWon;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.hasWon = false;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    @Override
    public String toString() {
        return name + " [Position: " + position + "]";
    }
}
