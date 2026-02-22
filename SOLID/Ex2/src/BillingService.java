import java.util.*;

public class BillingService {
    private final TaxCalculator taxCalculator;
    private final DiscountCalculator discountCalculator;
    private final Map<String, MenuItem> menu;

    public BillingService(TaxCalculator taxCalculator, DiscountCalculator discountCalculator,
            Map<String, MenuItem> menu) {
        this.taxCalculator = taxCalculator;
        this.discountCalculator = discountCalculator;
        this.menu = menu;
    }

    public Invoice createInvoice(String id, String customerType, List<OrderLine> orderLines) {
        List<Invoice.LineSummary> lineSummaries = new ArrayList<>();
        double subtotal = 0;

        for (OrderLine ol : orderLines) {
            MenuItem item = menu.get(ol.itemId);
            if (item == null)
                continue;

            double lineTotal = item.price * ol.qty;
            lineSummaries.add(new Invoice.LineSummary(item.name, ol.qty, lineTotal));
            subtotal += lineTotal;
        }

        double taxPct = taxCalculator.taxPercent(customerType);
        double tax = subtotal * (taxPct / 100.0);
        double discount = discountCalculator.discountAmount(customerType, subtotal, orderLines.size());
        double total = subtotal + tax - discount;

        return new Invoice(id, lineSummaries, subtotal, taxPct, tax, discount, total);
    }
}
