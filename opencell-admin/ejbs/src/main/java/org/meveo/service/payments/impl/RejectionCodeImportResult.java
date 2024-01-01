package org.meveo.service.payments.impl;

import java.util.List;

public class RejectionCodeImportResult {

    public static final RejectionCodeImportResult EMPTY_IMPORT_RESULT =
            new RejectionCodeImportResult(0, 0, 0, null);
    private final int lineToImportCount;
    private final int successCount;
    private final int errorCount;
    private final List<String> errors;

    public RejectionCodeImportResult(int lineToImportCount, int successCount, int errorCount, List<String> errors) {
        this.lineToImportCount = lineToImportCount;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.errors = errors;
    }

    public int getLineToImportCount() {
        return lineToImportCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public List<String> getErrors() {
        return errors;
    }
}
