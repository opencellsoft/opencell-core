package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Mbarek-Ay.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetGroupedAttributesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetGroupedAttributesResponse extends BaseResponse{

	private GroupedAttributeDto groupedAttributeDto;
	
	public GetGroupedAttributesResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the groupedAttributeDto
	 */
	public GroupedAttributeDto getGroupedAttributeDto() {
		return groupedAttributeDto;
	}

	/**
	 * @param groupedAttributeDto the groupedAttributeDto to set
	 */
	public void setGroupedAttributeDto(GroupedAttributeDto groupedAttributeDto) {
		this.groupedAttributeDto = groupedAttributeDto;
	}

	
	
	
}
