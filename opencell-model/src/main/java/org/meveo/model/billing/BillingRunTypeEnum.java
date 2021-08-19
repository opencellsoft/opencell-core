package org.meveo.model.billing;

public enum BillingRunTypeEnum {

	CYCLE(1, "BillingRunTypeEnum.cycle"), // the BR is attached to a BillingCycle
	EXCEPTIONAL(2, "BillingRunTypeEnum.exceptional"); // No BillingCycle is attached to a the BR
	

    private Integer id;
    private String label;

    BillingRunTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;

    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets enum by its id.
     * 
     * @param id id of billing run status
     * @return instance of BillingRunAutomaticActionsEnum
     */
    public static BillingRunTypeEnum getValue(Integer id) {
        if (id != null) {
            for (BillingRunTypeEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}