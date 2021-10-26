/*
 */
package org.meveo.model.dunning;

public enum PayRetryFrequencyUnitEnum {


	  /**
     * customer level
     */
    DAY(1,"PayRetryFrequencyUnitEnum.day"),

    /**
     * invoice level
     */
    MONTH(1,"PayRetryFrequencyUnitEnum.month");



	private Integer id;
	private String label;

	PayRetryFrequencyUnitEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public static PayRetryFrequencyUnitEnum getValue(Integer id) {
		if (id != null) {
			for (PayRetryFrequencyUnitEnum value : values()) {
				if (value.getId().intValue() == id.intValue()) {
					return value;
				}
			}
		}
		return null;
	}
}
