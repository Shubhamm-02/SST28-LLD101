public abstract class Exporter {
    /**
     * Exports the request.
     * Contract: Should NOT throw RuntimeExceptions for valid requests.
     * If formatting fails due to constraints (like length), return
     * ExportResult.error().
     */
    public abstract ExportResult export(ExportRequest req);
}
