package org.meveo.model.payments;

/**
 * Payment Method types.
 * 
 * @author HHANINE
 */
public enum RecurrenceUnitEnum {
	MONTH(1, "Month"), DAY(2, "Day");

    private String label;
    private Integer id;

    RecurrenceUnitEnum(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return id;
    }

    public static RecurrenceUnitEnum getValue(Integer id) {
        if (id != null) {
            for (RecurrenceUnitEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
