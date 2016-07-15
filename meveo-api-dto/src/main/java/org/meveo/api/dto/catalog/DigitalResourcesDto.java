package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.DigitalResource;

@XmlRootElement(name = "DigitalResource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DigitalResourcesDto implements Serializable {

	private static final long serialVersionUID = 5517448250177253851L;

	private String code;

	private String description;

	private String uri;

	private String mimeType;
	
	private boolean disabled;

	public DigitalResourcesDto() {
	}

	public DigitalResourcesDto(DigitalResource resource) {
		this.setCode(resource.getCode());
		this.setDescription(resource.getDescription());
		this.setUri(resource.getUri());
		this.setMimeType(resource.getMimeType());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
