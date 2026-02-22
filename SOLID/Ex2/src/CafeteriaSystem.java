import java.util.*;

public class CafeteriaSystem {
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final InvoiceRepository repository;
    private final BillingService billingService;
    private int invoiceSeq = 1000;

    public CafeteriaSystem() {
        this.repository = new FileStore();
        this.billingService = new BillingService(new TaxRules(), new DiscountRules(), menu);
    }

    public void addToMenu(MenuItem i) {
        menu.put(i.id, i);
    }

    public void checkout(String customerType, List<OrderLine> lines) {
        String invId = "INV-" + (++invoiceSeq);

        // 1. Calculate (SRP: BillingService)
        Invoice invoice = billingService.createInvoice(invId, customerType, lines);

        // 2. Format (SRP: InvoiceFormatter)
        String printable = InvoiceFormatter.format(invoice);
        System.out.print(printable);

        // 3. Save (SRP: InvoiceRepository)
        repository.save(invId, printable);
        System.out.println("Saved invoice: " + invId + " (lines=" + repository.countLines(invId) + ")");
    }
}