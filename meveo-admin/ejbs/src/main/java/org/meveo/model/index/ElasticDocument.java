package org.meveo.model.index;

import java.util.Date;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.model.BusinessEntity;

public class ElasticDocument {
	
	private String  code;
	private String  description;
	private Date    created;
	private String  creator;
	private CustomFieldsDto customFieldsDto;
	
	public ElasticDocument(){
		
	}
	


	public ElasticDocument(BusinessEntity entity) {
		this.code         = entity.getCode();
		this.description  = entity.getDescription();
		this.created      = entity.getAuditable().getCreated();
		this.creator      = entity.getAuditable().getCreator().getUserName();
	}

	public ElasticDocument(BusinessEntity entity, String creatorUsername) {
		this.code         = entity.getCode();
		this.description  = entity.getDescription();
		this.created      = entity.getAuditable().getCreated();
		this.creator      = creatorUsername;
	}


	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}



	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}



	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}



	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
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
	 * @return the customFieldsDto
	 */
	public CustomFieldsDto getCustomFieldsDto() {
		return customFieldsDto;
	}


	/**
	 * @param customFieldsDto the customFieldsDto to set
	 */
	public void setCustomFieldsDto(CustomFieldsDto customFieldsDto) {
		this.customFieldsDto = customFieldsDto;
	}



	public String toJson() {
		return JsonUtils.toJson(this);
	}

}
