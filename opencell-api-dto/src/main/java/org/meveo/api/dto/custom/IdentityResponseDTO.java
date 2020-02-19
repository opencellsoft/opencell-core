package org.meveo.api.dto.custom;

import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.response.BaseResponse;

public class IdentityResponseDTO extends BaseResponse {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdentityResponseDTO(Long id) {
		super();
		this.id = id;
	}

	@XmlAttribute(name = "id")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
