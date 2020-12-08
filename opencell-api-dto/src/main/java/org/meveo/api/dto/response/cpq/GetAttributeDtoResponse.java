package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Mbarek-Ay.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetAttributeDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAttributeDtoResponse extends BaseResponse{

	private AttributeDTO attributeDto;
	
	public GetAttributeDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the attributeDto
	 */
	public AttributeDTO getAttributeDto() {
		return attributeDto;
	}

	/**
	 * @param attributeDto the attributeDto to set
	 */
	public void setAttributeDto(AttributeDTO attributeDto) {
		this.attributeDto = attributeDto;
	}
	 
	
	
	
}
