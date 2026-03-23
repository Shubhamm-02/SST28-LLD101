public class Main {
    public static void main(String[] args) {

        Pen ballPen = PenFactory.createPen("BALL");
        ballPen.write();

        System.out.println();

        Pen gelPen = PenFactory.createPen("GEL");
        gelPen.write();
    }
}