package org.meveo.api.dto.cpq;

import org.meveo.model.cpq.GroupedService;

public class GroupedServiceDto {

	private String productCode;
	private String code;
	private String description;
	
	public GroupedServiceDto() {
		
	}
	
	public GroupedServiceDto(GroupedService groupedService) {
		if(groupedService != null) {
			this.code = groupedService.getCode();
			this.description = groupedService.getDescription();
			
			if(groupedService.getProduct() != null) {
				this.productCode = groupedService.getProduct().getCode();
			}
		}
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
