package org.meveo.api.dto.response.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "MatchedOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchedOperationsResponseDto extends BaseResponse {

    private static final long serialVersionUID = -1208828631493594330L;

    @XmlElementWrapper(name = "matchedOperations")
    @XmlElement(name = "matchedOperation")
    List<MatchedOperationDto> matchedOperations = new ArrayList<>();

    public List<MatchedOperationDto> getMatchedOperations() {
        return matchedOperations;
    }

    public void setMatchedOperations(List<MatchedOperationDto> matchedOperations) {
        this.matchedOperations = matchedOperations;
    }
}