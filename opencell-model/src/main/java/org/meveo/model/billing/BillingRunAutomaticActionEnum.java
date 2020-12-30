package org.meveo.model.billing;

public enum BillingRunAutomaticActionEnum {

    CANCEL(1, "BillingRunAutomaticActionsEnum.cancel"), // automatically cancels invoices rejected by the validation script. Suspect invoices are considered valid
    MOVE(2, "BillingRunAutomaticActionsEnum.move"); // automatically moves rejected and suspect invoices to a new billing run
	

    private Integer id;
    private String label;

    BillingRunAutomaticActionEnum(Integer id, String label) {
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
    public static BillingRunAutomaticActionEnum getValue(Integer id) {
        if (id != null) {
            for (BillingRunAutomaticActionEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}