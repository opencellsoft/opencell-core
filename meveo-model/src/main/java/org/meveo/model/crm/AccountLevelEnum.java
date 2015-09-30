package org.meveo.model.crm;

public enum AccountLevelEnum {
    CUST("account"), CA("account"), BA("account"), UA("account"), SUB("subscription"), ACC("access"), CHARGE("chargeTemplate"), OFFER("offerTemplate"), SERVICE("serviceTemplate"), TIMER(
            "jobInstance"), PROVIDER("provider"), SELLER("seller");

    private final String relationFieldname;

    private AccountLevelEnum(String relationFieldname) {
        this.relationFieldname = relationFieldname;
    }

    public String getLabel() {
        return "enum.accountLevel." + this.name();
    }

    public String getRelationFieldname() {
        return relationFieldname;
    }
}