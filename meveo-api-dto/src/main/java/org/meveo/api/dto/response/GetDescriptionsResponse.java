package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CatMessagesDto;

@XmlRootElement(name = "GetDescriptionsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDescriptionsResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private CatMessagesDto catMessagesDto;

	public CatMessagesDto getCatMessagesDto() {
		return catMessagesDto;
	}

	public void setCatMessagesDto(CatMessagesDto catMessagesDto) {
		this.catMessagesDto = catMessagesDto;
	}

	@Override
	public String toString() {
		return "GetDescriptionsResponse [catMessagesDto=" + catMessagesDto
				+ "]";
	}
}
