package org.meveo.model.catalog;

public enum CounterTypeEnum {

    USAGE(1, "counterTypeEnum.usage"),
    NOTIFICATION(2, "counterTypeEnum.notification");

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
