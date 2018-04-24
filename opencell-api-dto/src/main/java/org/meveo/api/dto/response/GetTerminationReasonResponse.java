package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TerminationReasonDto;

/**
 * The Class GetTerminationReasonResponse.
 */
@XmlRootElement(name = "TerminationReasonResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTerminationReasonResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The termination reason. */
    private List<TerminationReasonDto> terminationReason = new ArrayList<TerminationReasonDto>();

    /**
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public List<TerminationReasonDto> getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(List<TerminationReasonDto> terminationReason) {
        this.terminationReason = terminationReason;
    }

    @Override
    public String toString() {
        return "GetTerminationReasonResponse [terminationReason=" + terminationReason + "]";
    }
}