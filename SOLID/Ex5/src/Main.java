public class Main {
    public static void main(String[] args) {
        System.out.println("=== Export Demo ===");

        ExportRequest req = new ExportRequest("Weekly Report", SampleData.longBody());
        Exporter pdf = new PdfExporter();
        Exporter csv = new CsvExporter();
        Exporter json = new JsonExporter();
        Exporter xml = new XmlExporter();

        System.out.println("PDF: " + safe(pdf, req));
        System.out.println("CSV: " + safe(csv, req));
        System.out.println("JSON: " + safe(json, req));
        // System.out.println("XML: " + safe(xml, req));
    }

    private static String safe(Exporter e, ExportRequest r) {
        try {
            ExportResult out = e.export(r);
            if (out.success) {
                return "OK bytes=" + out.bytes.length;
            } else {
                return "ERROR: " + out.error;
            }
        } catch (RuntimeException ex) {
            return "ERROR: " + ex.getMessage();
        }
    }
}
