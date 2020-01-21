package org.meveo.model.catalog;

public enum CounterTypeEnum {

    USAGE(1, "counterTypeEnum.usage", false), NOTIFICATION(2, "counterTypeEnum.notification", false), USAGE_AMOUNT(3, "counterTypeEnum.usageAmount", true);
    private Integer id;
    private String label;
    private boolean isAccumulator;

    /**
     * @param id            the ID
     * @param label         the label
     * @param isAccumulator true if is it an accumulator counter type
     */
    CounterTypeEnum(Integer id, String label, boolean isAccumulator) {
        this.id = id;
        this.label = label;
        this.isAccumulator = isAccumulator;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isAccumulator() {
        return isAccumulator;
    }

    public static CounterTypeEnum getValue(Integer id) {
        if (id != null) {
            for (CounterTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
