package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetTagDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTagDtoResponse extends BaseResponse{

	private TagDto tagDto;
	
	public GetTagDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}
	/**
	 * @return the tagDto
	 */
	public TagDto getTagDto() {
		return tagDto;
	}
	/**
	 * @param tagDto the tagDto to set
	 */
	public void setTagDto(TagDto tagDto) {
		this.tagDto = tagDto;
	}

	
	
}
