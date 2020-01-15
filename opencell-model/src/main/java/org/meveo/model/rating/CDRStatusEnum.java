package org.meveo.model.rating;

public enum CDRStatusEnum {

	 OPEN(1, "cdrStatus.open"), PROCESSED(2, "cdrStatus.processed"), CLOSED(3, "cdrStatus.closed"), DISCARDED(4, "cdrStatus.discarded"), ERROR(5, "cdrStatus.error");

    private Integer id;
    private String label;

    private CDRStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static CDRStatusEnum getValue(Integer id) {
        if (id != null) {
            for (CDRStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
    public static CDRStatusEnum getByLabel(String label) {
        if (label != null) {
            for (CDRStatusEnum status : values()) {
                if (label.equals(status.getLabel())) {
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
