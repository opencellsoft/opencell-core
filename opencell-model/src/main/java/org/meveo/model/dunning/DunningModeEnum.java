/*
 */
package org.meveo.model.dunning;

public enum DunningModeEnum {

	  
	  /**
     * customer level
     */
    CUSTOMER_LEVEL(1,"dunningModeEnum.customer_level"),
    
    /**
     * invoice level
     */
    INVOICE_LEVEL(1,"dunningModeEnum.invoice_level");

    

	private Integer id;
	private String label;

	DunningModeEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public static DunningModeEnum getValue(Integer id) {
		if (id != null) {
			for (DunningModeEnum mode : values()) {
				if (mode.getId().intValue() == id.intValue()) {
					return mode;
				}
			}
		}
		return null;
	}
}
