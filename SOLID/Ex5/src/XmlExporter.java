import java.nio.charset.StandardCharsets;

public class XmlExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        if (req == null)
            return ExportResult.error("Null request");
        String xml = "<report><title>" + req.title + "</title><body>" + req.body + "</body></report>";
        return ExportResult.ok("application/xml", xml.getBytes(StandardCharsets.UTF_8));
    }
}
