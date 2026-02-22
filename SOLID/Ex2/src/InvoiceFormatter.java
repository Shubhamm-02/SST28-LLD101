public class InvoiceFormatter {
    public static String format(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice# ").append(invoice.id).append("\n");
        for (Invoice.LineSummary line : invoice.lines) {
            sb.append("- ").append(line.itemName)
                    .append(" x").append(line.qty)
                    .append(" = ").append(String.format("%.2f", line.lineTotal))
                    .append("\n");
        }
        sb.append("Subtotal: ").append(String.format("%.2f", invoice.subtotal)).append("\n");
        sb.append("Tax(").append((int) invoice.taxPct).append("%): ").append(String.format("%.2f", invoice.tax))
                .append("\n");
        sb.append("Discount: -").append(String.format("%.2f", invoice.discount)).append("\n");
        sb.append("TOTAL: ").append(String.format("%.2f", invoice.total)).append("\n");
        return sb.toString();
    }

    // pointless wrapper (smell)
    public static String identityFormat(String s) {
        return s;
    }
}