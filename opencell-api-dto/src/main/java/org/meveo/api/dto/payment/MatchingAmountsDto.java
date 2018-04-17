package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MatchingAmountsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MatchingAmounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8375302840183850003L;

    /** The matching amount. */
    private List<MatchingAmountDto> matchingAmount;

    /**
     * Gets the matching amount.
     *
     * @return the matching amount
     */
    public List<MatchingAmountDto> getMatchingAmount() {
        return matchingAmount;
    }

    /**
     * Sets the matching amount.
     *
     * @param matchingAmount the new matching amount
     */
    public void setMatchingAmount(List<MatchingAmountDto> matchingAmount) {
        this.matchingAmount = matchingAmount;
    }
}