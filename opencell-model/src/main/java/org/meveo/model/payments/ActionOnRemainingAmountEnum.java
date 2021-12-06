package org.meveo.model.payments;

/**
 * Payment Method types.
 * 
 * @author HHANINE
 */
public enum ActionOnRemainingAmountEnum {
	FIRST(1, "First"), LAST(2, "Last"), ADDITIONAL(3, "Additional");

    private String label;
    private Integer id;

    ActionOnRemainingAmountEnum(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return id;
    }

    public static ActionOnRemainingAmountEnum getValue(Integer id) {
        if (id != null) {
            for (ActionOnRemainingAmountEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}