public class ExportResult {
    public final String contentType;
    public final byte[] bytes;
    public final boolean success;
    public final String error;

    private ExportResult(String contentType, byte[] bytes, boolean success, String error) {
        this.contentType = contentType;
        this.bytes = bytes;
        this.success = success;
        this.error = error;
    }

    public static ExportResult ok(String contentType, byte[] bytes) {
        return new ExportResult(contentType, bytes, true, null);
    }

    public static ExportResult error(String msg) {
        return new ExportResult(null, new byte[0], false, msg);
    }
}
