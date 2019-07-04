package org.meveo.model.scripts;

public enum RevenueRecognitionDelayUnitEnum {

    MONTH(1, "revenueRecognitionDelayUnitEnum.MONTH"), DAY(2, "revenueRecognitionDelayUnitEnum.DAY");

    private Integer id;
    private String label;

    RevenueRecognitionDelayUnitEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static RevenueRecognitionDelayUnitEnum getValue(Integer id) {
        if (id != null) {
            for (RevenueRecognitionDelayUnitEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    public String toString() {
        return label.toString();
    }
}
