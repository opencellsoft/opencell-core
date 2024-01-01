package org.meveo.service.payments.impl;

import org.meveo.apiv2.payments.RejectionCodeImportMode;

public class ImportRejectionCodeConfig {

    private final String base64Csv;
    private final boolean ignoreLanguageErrors;
    private final RejectionCodeImportMode mode;

    public ImportRejectionCodeConfig(String base64Csv,
                                     boolean ignoreLanguageErrors, RejectionCodeImportMode mode) {
        this.base64Csv = base64Csv;
        this.ignoreLanguageErrors = ignoreLanguageErrors;
        this.mode = mode;
    }

    public String getBase64Csv() {
        return base64Csv;
    }

    public boolean isIgnoreLanguageErrors() {
        return ignoreLanguageErrors;
    }

    public RejectionCodeImportMode getMode() {
        return mode;
    }
}
