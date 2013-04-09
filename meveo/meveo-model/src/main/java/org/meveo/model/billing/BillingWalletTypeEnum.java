package org.meveo.model.billing;

public enum BillingWalletTypeEnum {
	
    POSTPAID(1, "BillingWalletTypeEnum.postpaid"),
    PREPAID(2, "BillingWalletTypeEnum.prepaid");

    private Integer id;
    private String label;

    BillingWalletTypeEnum(Integer id, String label) {
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
     */
    public static BillingWalletTypeEnum getValue(Integer id) {
        if (id != null) {
            for (BillingWalletTypeEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
