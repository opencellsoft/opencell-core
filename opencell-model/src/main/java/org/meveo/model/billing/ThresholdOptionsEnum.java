package org.meveo.model.billing;

public enum ThresholdOptionsEnum {
    BEFORE_DISCOUNT(1, "thresholdOptionsEnum.beforeDiscount"), AFTER_DISCOUNT(2, "thresholdOptionsEnum.afterDiscount"),
    POSITIVE_RT(3, "thresholdOptionsEnum.positiveRT"), POSITIVE_IL(4, "thresholdOptionsEnum.positiveIL");

    private Integer id;
    private String label;

    /**
     * Default constructor.
     *
     * @param id
     * @param label
     */
    ThresholdOptionsEnum(Integer id, String label){
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
    public String toString() {
        return name();
    }
}
