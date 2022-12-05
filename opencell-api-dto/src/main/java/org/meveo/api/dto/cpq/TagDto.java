package org.meveo.api.dto.cpq;

import jakarta.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.tags.Tag;

import io.swagger.v3.oas.annotations.media.Schema;

public class TagDto extends BusinessEntityDto {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	@Schema(description = "code of the seller")
	private String sellerCode;
    @NotNull
	@Schema(description = "name of the tag")
	private String name;
	@NotNull
	@Schema(description = "code of the type of the tag")
	private String tagTypeCode;
	@Schema(description = "code of the tag parent")
	private String parentTagCode;
	@Schema(description = "filter expression language")
	private String filterEl;
	
	public TagDto() {}
	public TagDto(Tag tag) {
		if(tag != null) {
			this.code = tag.getCode();
			this.description = tag.getDescription(); 
			this.id = tag.getId();
			this.name = tag.getName();
			if(tag.getSeller() != null) {
				this.sellerCode = tag.getSeller().getCode();
			}
			if(tag.getTagType() != null) {
				this.tagTypeCode = tag.getTagType().getCode();
			}
			if(tag.getParentTag() != null) {
				this.parentTagCode = tag.getParentTag().getCode();
			}
			this.filterEl = tag.getFilterEl();
			
		}
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the filterEl
	 */
	public String getFilterEl() {
		return filterEl;
	}
	/**
	 * @param filterEl the filterEl to set
	 */
	public void setFilterEl(String filterEl) {
		this.filterEl = filterEl;
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
	/**
	 * @return the tagTypeCode
	 */
	public String getTagTypeCode() {
		return tagTypeCode;
	}
	/**
	 * @param tagTypeCode the tagTypeCode to set
	 */
	public void setTagTypeCode(String tagTypeCode) {
		this.tagTypeCode = tagTypeCode;
	}
	/**
	 * @return the parentTagCode
	 */
	public String getParentTagCode() {
		return parentTagCode;
	}
	/**
	 * @param parentTagCode the parentTagCode to set
	 */
	public void setParentTagCode(String parentTagCode) {
		this.parentTagCode = parentTagCode;
	}

	@Override
	public String toString() {
		return "TagDto [id=" + id + ", sellerCode=" + sellerCode + ", name=" + name + ", tagTypeCode=" + tagTypeCode
				+ ", parentTagCode=" + parentTagCode + ", filterEl=" + filterEl + ", description=" + description + ", updatedCode="
				+ updatedCode + "]";
	}
	
	
	
	
	
	
}
