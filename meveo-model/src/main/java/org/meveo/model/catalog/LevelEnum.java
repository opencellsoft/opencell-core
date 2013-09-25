package org.meveo.model.catalog;

public enum LevelEnum {
    PROVIDER(1, "levelEnum.provider"),
    SELLER(2, "levelEnum.seller"),
    CUSTOMER(3, "levelEnum.customer"),
    CUSTOMER_ACCOUNT(4, "levelEnum.customerAccount"),
    BILLING_ACCOUNT(5, "levelEnum.billingAccount"),
    USER_ACCOUNT(5, "levelEnum.userAccount");

    private Integer id;
    private String label;

    LevelEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static LevelEnum getValue(Integer id) {
        if (id != null) {
            for (LevelEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
