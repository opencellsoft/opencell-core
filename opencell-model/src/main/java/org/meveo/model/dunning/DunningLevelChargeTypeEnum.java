package org.meveo.model.dunning;

public enum DunningLevelChargeTypeEnum {

    /**
     * Flat amount
     */
    FLAT_AMOUNT(1,"dunningLevelChargeTypeEnum.flat_amount"),

    /**
     * Percentage
     */
    PERCENTAGE(2,"dunningLevelChargeTypeEnum.percentage");



    private Integer id;
    private String label;

    DunningLevelChargeTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static DunningLevelChargeTypeEnum getValue(Integer id) {
        if (id != null) {
            for (DunningLevelChargeTypeEnum type : values()) {
                if (type.getId().intValue() == id.intValue()) {
                    return type;
                }
            }
        }
        return null;
    }
}
