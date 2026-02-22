import java.nio.charset.StandardCharsets;

public class JsonExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        if (req == null)
            return ExportResult.error("Null request");
        String json = "{\"title\":\"" + escape(req.title) + "\",\"body\":\"" + escape(req.body) + "\"}";
        return ExportResult.ok("application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\"", "\\\"");
    }
}
