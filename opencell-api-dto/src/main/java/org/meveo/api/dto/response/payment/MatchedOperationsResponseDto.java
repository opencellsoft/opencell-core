package org.meveo.api.dto.response.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class MatchedOperationsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "MatchedOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchedOperationsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1208828631493594330L;

    /** The matched operations. */
    @XmlElementWrapper(name = "matchedOperations")
    @XmlElement(name = "matchedOperation")
    List<MatchedOperationDto> matchedOperations = new ArrayList<>();

    /**
     * Gets the matched operations.
     *
     * @return the matched operations
     */
    public List<MatchedOperationDto> getMatchedOperations() {
        return matchedOperations;
    }

    /**
     * Sets the matched operations.
     *
     * @param matchedOperations the new matched operations
     */
    public void setMatchedOperations(List<MatchedOperationDto> matchedOperations) {
        this.matchedOperations = matchedOperations;
    }
}