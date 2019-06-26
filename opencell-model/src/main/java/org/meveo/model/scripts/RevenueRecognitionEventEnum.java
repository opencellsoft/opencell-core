package org.meveo.model.scripts;

public enum RevenueRecognitionEventEnum {
    SUBSCRIPTION_START(1, "revenueRecognitionEvent.SUBSCRIPTION_START"), SUBSCRIPTION_STOP(2, "revenueRecognitionEvent.SUBSCRIPTION_STOP"), INVOICE_DATE(3,
            "revenueRecognitionEvent.INVOICE_DATE"), INVOICE_DUE_DATE(4, "revenueRecognitionEvent.INVOICE_DUE_DATE"), SERVICE_PERIOD_START(5,
                    "revenueRecognitionEvent.SERVICE_PERIOD_START"), SERVICE_PERIOD_STOP(6, "revenueRecognitionEvent.SERVICE_PERIOD_STOP");

    private Integer id;
    private String label;

    RevenueRecognitionEventEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static RevenueRecognitionEventEnum getValue(Integer id) {
        if (id != null) {
            for (RevenueRecognitionEventEnum status : values()) {
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
