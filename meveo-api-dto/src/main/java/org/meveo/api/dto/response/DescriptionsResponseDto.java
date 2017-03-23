package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DescriptionsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DescriptionsResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private CatMessagesListDto catMessages = new CatMessagesListDto();

	public CatMessagesListDto getCatMessages() {
		return catMessages;
	}

	public void setCatMessages(CatMessagesListDto catMessages) {
		this.catMessages = catMessages;
	}

	@Override
	public String toString() {
		return "CatMessagesListResponseDto [catMessages=" + catMessages + "]";
	}
}
