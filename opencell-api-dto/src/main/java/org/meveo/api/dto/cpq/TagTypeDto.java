package org.meveo.api.dto.cpq;

import javax.validation.constraints.NotNull;

import org.meveo.model.cpq.tags.TagType;

public class TagTypeDto {

	@NotNull
	private String code;
    private String description;
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
}
