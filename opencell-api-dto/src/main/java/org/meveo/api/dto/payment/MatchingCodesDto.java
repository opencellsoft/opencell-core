package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MatchingCodesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MatchingCodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingCodesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5230851336194617883L;

    /** The matching code. */
    private List<MatchingCodeDto> matchingCode;

    /**
     * Gets the matching code.
     *
     * @return the matching code
     */
    public List<MatchingCodeDto> getMatchingCode() {
        if (matchingCode == null)
            matchingCode = new ArrayList<MatchingCodeDto>();
        return matchingCode;
    }

    /**
     * Sets the matching code.
     *
     * @param matchingCode the new matching code
     */
    public void setMatchingCode(List<MatchingCodeDto> matchingCode) {
        this.matchingCode = matchingCode;
    }

}
