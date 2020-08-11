package org.meveo.model.document;

/**
 *
 * @author Abdelkader Bouazza
 * @lastModifiedVersion 10.0.0
 */

public enum DocumentStatus {
    ACTIVE(1, "DocumentStatus.active"),
    INACTIVE(2, "DocumentStatus.inactive"),
    REJECTED(3, "DocumentStatus.rejected");

    private Integer id;
    private String label;

    DocumentStatus(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return this.id;
    }
}
