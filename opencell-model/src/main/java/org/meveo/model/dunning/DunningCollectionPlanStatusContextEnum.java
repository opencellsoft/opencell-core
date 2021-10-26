package org.meveo.model.dunning;

public enum DunningCollectionPlanStatusContextEnum {
    /**
     * For ongoing collection plans that are following the dunning levels defined in the related dunning policy.
     * The status is defined for each dunning level in dunning level configuration.
     */
    ACTIVE_DUNNING(1, "DunningInvoiceStatusContextEnum.active_dunning"),
    /**
     * When collection plan is interrupted because invoice is paid
     */
    SUCCESSFUL_DUNNING(2, "DunningInvoiceStatusContextEnum.failed_dunning"),

    /**
     * When collection plan reaches the end of dunning level and balance is still positive.
     * The status is defined in the end of dunning level for each policy and can be different for each policy depending on the next step.
     */
    FAILED_DUNNING(3, "DunningInvoiceStatusContextEnum.failed_dunning"),
    /**
     * When user pauses a collection plan
     */
    PAUSED_DUNNING(4, "DunningInvoiceStatusContextEnum.paused_dunning"),
    /**
     * When user stop a collection plan
     */
    STOPPED_DUNNING(5, "DunningInvoiceStatusContextEnum.stopped_dunning");

    private Integer id;
    private String label;

    DunningCollectionPlanStatusContextEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static DunningCollectionPlanStatusContextEnum getValue(Integer id) {
        if (id != null) {
            for (DunningCollectionPlanStatusContextEnum context : values()) {
                if (context.getId().intValue() == id.intValue()) {
                    return context;
                }
            }
        }
        return null;
    }
}
