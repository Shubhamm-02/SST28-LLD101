package models;

public class Ladder {
    private int start;
    private int end;

    public Ladder(int start, int end) {
        if (end <= start) {
            throw new IllegalArgumentException("Ladder end must be greater than start. Start: " + start + ", End: " + end);
        }
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Ladder{" + start + " -> " + end + "}";
    }
}
