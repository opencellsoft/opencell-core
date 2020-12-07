package org.meveo.api.dto.cpq;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.tags.TagType;

public class TagTypeDto extends BusinessEntityDto {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sellerCode;
	
	public TagTypeDto() {
		
	}
	
	public TagTypeDto(TagType tagType) {
		if(tagType != null) {
			this.code = tagType.getCode();
			this.description = tagType.getDescription();
			this.sellerCode = tagType.getSeller() != null ? tagType.getSeller().getCode() : null;
		}
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
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}

	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	@Override
	public String toString() {
		return "TagTypeDto [sellerCode=" + sellerCode + ", id=" + id + ", code=" + code + ", description=" + description
				+ ", updatedCode=" + updatedCode + "]";
	}
	
	
	
}
