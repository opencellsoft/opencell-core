package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
	
	private Long id;
	
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
	
}
