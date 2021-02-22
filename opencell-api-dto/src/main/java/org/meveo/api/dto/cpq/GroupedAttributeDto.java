package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;

public class GroupedAttributeDto {

	private Long id;
	
    @NotNull
	private String code;
	private String description;
	private List<AttributeDTO> attributes = new ArrayList<AttributeDTO>();
	private Set<String> attributeCodes = new HashSet<String>();
	private boolean display;
	private boolean disabled;
	private boolean mandatory;
	private boolean selectable=Boolean.TRUE;  
	private boolean ruled=Boolean.FALSE;
	 private List<String> commercialRuleCodes=new ArrayList<String>();
	
	public GroupedAttributeDto() {
		
	}
	
	public GroupedAttributeDto(GroupedAttributes groupedService) {
		if(groupedService != null) {
			this.code = groupedService.getCode();
			this.description = groupedService.getDescription();
			this.display = groupedService.getDisplay();
			this.disabled=groupedService.isDisabled();
			this.id = groupedService.getId();
		}
	}
	
	public GroupedAttributeDto(GroupedAttributes groupedAttribues, List<Attribute> attributes) {
		this(groupedAttribues);
		if(attributes != null)
			attributes.forEach( attribute -> {
				this.attributes.add(new AttributeDTO(attribute));
			});
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
	 * @return the attributes
	 */
	public List<AttributeDTO> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<AttributeDTO> attributes) {
		this.attributes = attributes;
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

	/**
	 * @return the commercialRuleCodes
	 */
	public List<String> getCommercialRuleCodes() {
		return commercialRuleCodes;
	}

	/**
	 * @param commercialRuleCodes the commercialRuleCodes to set
	 */
	public void setCommercialRuleCodes(List<String> commercialRuleCodes) {
		this.commercialRuleCodes = commercialRuleCodes;
	}

	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @return the ruled
	 */
	public boolean isRuled() {
		return ruled;
	}

	/**
	 * @param ruled the ruled to set
	 */
	public void setRuled(boolean ruled) {
		this.ruled = ruled;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the attributeCodes
	 */
	public Set<String> getAttributeCodes() {
		return attributeCodes;
	}

	/**
	 * @param attributeCodes the attributeCodes to set
	 */
	public void setAttributeCodes(Set<String> attributeCodes) {
		this.attributeCodes = attributeCodes;
	}

	

	
	
}
