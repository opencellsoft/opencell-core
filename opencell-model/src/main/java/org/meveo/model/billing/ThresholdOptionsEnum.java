package org.meveo.model.billing;

public enum ThresholdOptionsEnum {
    BEFORE_DISCOUNT(1, "thresholdOptionsEnum.beforeDiscount"),
    AFTER_DISCOUNT(2, "thresholdOptionsEnum.beforeDiscount"),
    POSITIVE_RT(3, "thresholdOptionsEnum.positiveRT");

    private Integer id;
    private String label;

    /**
     * Default constructor.
     * @param id
     * @param label
     */
    private ThresholdOptionsEnum(Integer id, String label){
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
