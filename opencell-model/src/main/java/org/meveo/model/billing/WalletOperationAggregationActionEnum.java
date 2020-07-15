package org.meveo.model.billing;

public enum WalletOperationAggregationActionEnum {
    KEY(1, "WalletOperationAggregationActionEnum.key"), //
    EMPTY(2, "WalletOperationAggregationActionEnum.empty"), //
    VALUE(3, "WalletOperationAggregationActionEnum.value"), //
    SUM(4, "WalletOperationAggregationActionEnum.sum"), //
    COUNT(5, "WalletOperationAggregationActionEnum.count"), //
    AVG(6, "WalletOperationAggregationActionEnum.average"), //
    MIN(7, "WalletOperationAggregationActionEnum.min"), //
    MAX(8, "WalletOperationAggregationActionEnum.max"), //
    TRUNCATE(9, "WalletOperationAggregationActionEnum.truncate"), //
    CUSTOM(10, "WalletOperationAggregationActionEnum.stringAgg"); //

    private Integer id;
    private String label;

    /**
     * Default constructor.
     *
     * @param id
     * @param label
     */
    private WalletOperationAggregationActionEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return name();
    }
}
