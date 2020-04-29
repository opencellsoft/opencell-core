package org.meveo.model.catalog;

/**
 * The accumulator counter type enumeration.
 *
 * @author Khalid HORRI
 */
public enum AccumulatorCounterTypeEnum {
    MULTI_VALUE(1, "accumulatorCounterTypeEnum.multiValue"), SINGLE_VALUE(2, "accumulatorCounterTypeEnum.singleValue");
    private Integer id;
    private String label;

    /**
     * @param id    the ID
     * @param label the label
     */
    AccumulatorCounterTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static AccumulatorCounterTypeEnum getValue(Integer id) {
        if (id != null) {
            for (AccumulatorCounterTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
