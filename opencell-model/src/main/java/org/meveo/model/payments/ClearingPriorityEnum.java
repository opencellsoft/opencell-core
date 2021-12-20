package org.meveo.model.payments;

/**
 * Payment Method types.
 * 
 * @author HHANINE
 */
public enum ClearingPriorityEnum {
	NEWEST(1, "Newest"), OLDEST(2, "Oldest"), SMALLEST(3, "Smallest"), BIGGEST(4, "Biggest");

    private String label;
    private Integer id;

    ClearingPriorityEnum(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return id;
    }

    public static ClearingPriorityEnum getValue(Integer id) {
        if (id != null) {
            for (ClearingPriorityEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
