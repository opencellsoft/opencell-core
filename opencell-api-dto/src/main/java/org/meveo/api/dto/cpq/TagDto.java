package org.meveo.api.dto.cpq;

import org.meveo.model.cpq.tags.Tag;

public class TagDto {

	private String code;
    private String description;
    private Long id;
	private String sellerCode;
	private String name;
	private String tagTypeCode;
	private String parentTagCode;
	private String filterEl;
	
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
	
	
}
