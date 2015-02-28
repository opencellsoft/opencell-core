package org.meveo.admin.job;

public class ReportCustomFieldDto {

	private String description;
	private String value;
	
	
	
	public ReportCustomFieldDto(String description, String value) {
		super();
		this.description = description;
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
