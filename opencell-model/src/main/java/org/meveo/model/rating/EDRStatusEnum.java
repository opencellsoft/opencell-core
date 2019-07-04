package org.meveo.model.rating;

public enum EDRStatusEnum {

    OPEN(1, "edrStatus.open"), RATED(2, "edrStatus.rated"), REJECTED(3, "edrStatus.rejected"), MEDIATING(4, "edrStatus.mediating"), AGGREGATED(5, "edrStatus.aggregated");

    private Integer id;
    private String label;

    private EDRStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static EDRStatusEnum getValue(Integer id) {
        if (id != null) {
            for (EDRStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    public String toString() {
        return name();
    }

}
