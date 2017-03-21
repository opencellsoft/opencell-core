package org.meveo.model.catalog;

public enum RoundingModeEnum {

	NEAREST(1, "RoundingModeEnum.NEAREST"),
	DOWN(2, "RoundingModeEnum.DOWN"),
	UP(3, "RoundingModeEnum.UP");

    private Integer id;
    private String label;

    RoundingModeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static RoundingModeEnum getValue(Integer id) {
        if (id != null) {
            for (RoundingModeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
