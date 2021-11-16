package org.meveo.model.catalog;

public enum ChargeTemplateStatusEnum {
	DRAFT("ChargeTemplateStatusEnum.DRAFT"), ACTIVE("ChargeTemplateStatusEnum.ACTIVE"), ARCHIVED("ChargeTemplateStatusEnum.ARCHIVED");
	private String label;
	
	ChargeTemplateStatusEnum(String label) {
        this.setLabel(label);
    }

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}