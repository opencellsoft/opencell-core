package org.meveo.model.report.query;

public enum SortOrderEnum {
    DESCENDING("desc"), ASCENDING("asc");

    private String label;

    SortOrderEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}