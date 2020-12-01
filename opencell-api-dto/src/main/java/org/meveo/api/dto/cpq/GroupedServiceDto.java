package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.GroupedService;

public class GroupedServiceDto {

	private String productCode;
	private int prodcutVersion;
	private String code;
	private String description;
	private List<String> serviceCodes = new ArrayList<String>();
	private boolean display;
	private boolean mandatory;
	
	public GroupedServiceDto() {
		
	}
	
	public GroupedServiceDto(GroupedService groupedService) {
		if(groupedService != null) {
			this.code = groupedService.getCode();
			this.description = groupedService.getDescription();
			this.display = groupedService.getDisplay();
			if(groupedService.getProductVersion() != null && groupedService.getProductVersion().getProduct() != null) {
				this.productCode = groupedService.getProductVersion().getProduct().getCode();
			}
		}
	}
	
	public GroupedServiceDto(GroupedService groupedService, List<ServiceTemplate> serviceTemplateCodes) {
		this(groupedService);
		if(serviceTemplateCodes != null)
			serviceTemplateCodes.forEach( service -> {
				serviceCodes.add(service.getCode());
			});
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

	/**
	 * @return the prodcutVersion
	 */
	public int getProdcutVersion() {
		return prodcutVersion;
	}

	/**
	 * @param prodcutVersion the prodcutVersion to set
	 */
	public void setProdcutVersion(int prodcutVersion) {
		this.prodcutVersion = prodcutVersion;
	}

	/**
	 * @return the serviceCodes
	 */
	public List<String> getServiceCodes() {
		return serviceCodes;
	}

	/**
	 * @param serviceCodes the serviceCodes to set
	 */
	public void setServiceCodes(List<String> serviceCodes) {
		this.serviceCodes = serviceCodes;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
}
