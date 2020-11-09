package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetTagTypeDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTagTypeDtoResponse extends BaseResponse{

	private TagTypeDto tagTypeDto;

	public GetTagTypeDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the tagTypeDto
	 */
	public TagTypeDto getTagTypeDto() {
		return tagTypeDto;
	}

	/**
	 * @param tagTypeDto the tagTypeDto to set
	 */
	public void setTagTypeDto(TagTypeDto tagTypeDto) {
		this.tagTypeDto = tagTypeDto;
	}

	
	
}
