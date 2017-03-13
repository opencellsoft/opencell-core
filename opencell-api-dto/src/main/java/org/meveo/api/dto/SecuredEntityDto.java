package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.SecuredEntity;

@XmlRootElement(name = "SecuredEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecuredEntityDto extends BaseDto {

	private static final long serialVersionUID = 8941891021770440273L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String entityClass;

	public SecuredEntityDto() {
	}

	public SecuredEntityDto(SecuredEntity entity) {
		this.code = entity.getCode();
		this.entityClass = entity.getEntityClass();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

}
