import java.util.List;

public class Invoice {
    public final String id;
    public final List<LineSummary> lines;
    public final double subtotal;
    public final double taxPct;
    public final double tax;
    public final double discount;
    public final double total;

    public Invoice(String id, List<LineSummary> lines, double subtotal, double taxPct, double tax, double discount,
            double total) {
        this.id = id;
        this.lines = lines;
        this.subtotal = subtotal;
        this.taxPct = taxPct;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
    }

    public static class LineSummary {
        public final String itemName;
        public final int qty;
        public final double lineTotal;

        public LineSummary(String itemName, int qty, double lineTotal) {
            this.itemName = itemName;
            this.qty = qty;
            this.lineTotal = lineTotal;
        }
    }
}
