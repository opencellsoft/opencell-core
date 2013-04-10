package org.meveo.model.catalog;

public enum CounterTypeEnum {

    MONETARY(1, "counterTypeEnum.monetary"),
    DATA(2, "counterTypeEnum.data"),
    DURATION(3, "counterTypeEnum.duration"),
    QUANTITY(4, "counterTypeEnum.quantity");

    private Integer id;
    private String label;

    CounterTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
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
