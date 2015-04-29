package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.InboundRequestsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListInboundRequestResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListInboundRequestResponseDto extends BaseResponse {

	private static final long serialVersionUID = -1515932369680879496L;

	private InboundRequestsDto inboundRequests = new InboundRequestsDto();

	public InboundRequestsDto getInboundRequests() {
		return inboundRequests;
	}

	public void setInboundRequests(InboundRequestsDto inboundRequests) {
		this.inboundRequests = inboundRequests;
	}

	@Override
	public String toString() {
		return "ListInboundRequestResponseDto [inboundRequests=" + inboundRequests + ", toString()=" + super.toString() + "]";
	}

}
