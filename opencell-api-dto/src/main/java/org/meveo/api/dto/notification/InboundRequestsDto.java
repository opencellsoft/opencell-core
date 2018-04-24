package org.meveo.api.dto.notification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class InboundRequestsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InboutRequests")
@XmlAccessorType(XmlAccessType.FIELD)
public class InboundRequestsDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4968458684592803252L;

    /** The inbound request. */
    private List<InboundRequestDto> inboundRequest;

    /**
     * Gets the inbound request.
     *
     * @return the inbound request
     */
    public List<InboundRequestDto> getInboundRequest() {
        if (inboundRequest == null)
            inboundRequest = new ArrayList<InboundRequestDto>();
        return inboundRequest;
    }

    /**
     * Sets the inbound request.
     *
     * @param inboundRequest the new inbound request
     */
    public void setInboundRequest(List<InboundRequestDto> inboundRequest) {
        this.inboundRequest = inboundRequest;
    }

    @Override
    public String toString() {
        return "InboutRequestsDto [inboundRequest=" + inboundRequest + ", toString()=" + super.toString() + "]";
    }

}
