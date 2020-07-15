package org.meveo.model.billing;

public enum GroupByAggregationFunctionEnum {
    SIGN(1, "GroupByAggregationFunctionEnum.sign"), //
    FLOOR(2, "GroupByAggregationFunctionEnum.floor"), //
    CEILING(3, "GroupByAggregationFunctionEnum.ceiling"), //
    ROUND(4, "GroupByAggregationFunctionEnum.round"), //
    TRUNC(5, "GroupByAggregationFunctionEnum.truncate"), //
    MOD(6, "GroupByAggregationFunctionEnum.modulo"), //
    DIV(7, "GroupByAggregationFunctionEnum.devide"); //

    private Integer id;
    private String label;

    /**
     * Default constructor.
     *
     * @param id
     * @param label
     */
    private GroupByAggregationFunctionEnum(Integer id, String label) {
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
