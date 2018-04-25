package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.InboundRequestsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class InboundRequestsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InboundRequestsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InboundRequestsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1515932369680879496L;

    /** The inbound requests. */
    private InboundRequestsDto inboundRequests = new InboundRequestsDto();

    /**
     * Gets the inbound requests.
     *
     * @return the inbound requests
     */
    public InboundRequestsDto getInboundRequests() {
        return inboundRequests;
    }

    /**
     * Sets the inbound requests.
     *
     * @param inboundRequests the new inbound requests
     */
    public void setInboundRequests(InboundRequestsDto inboundRequests) {
        this.inboundRequests = inboundRequests;
    }

    @Override
    public String toString() {
        return "ListInboundRequestResponseDto [inboundRequests=" + inboundRequests + ", toString()=" + super.toString() + "]";
    }
}