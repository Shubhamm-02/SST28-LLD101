public class Pen {
    private Tip tip;
    private Refill refill;

    public Pen(Tip tip, Refill refill) {
        this.tip = tip;
        this.refill = refill;
    }

    public void write() {
        tip.writeTip();
        refill.getInk().writeInk();
        System.out.println("Pen is writing...");
    }
}