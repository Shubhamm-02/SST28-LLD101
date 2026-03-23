public class PenFactory {

    public static Pen createPen(String type) {

        if (type.equalsIgnoreCase("BALL")) {
            return new Pen(
                new FineTip(),
                new Refill(new BallInk())
            );
        }

        else if (type.equalsIgnoreCase("GEL")) {
            return new Pen(
                new MediumTip(),
                new Refill(new GelInk())
            );
        }

        return null;
    }
}