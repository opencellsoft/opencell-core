package org.meveo.api.dto.payment;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MatchingAmountDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "MatchingAmount")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountDto {

    /** The matching code. */
    private String matchingCode;
    
    /** The matching amount. */
    private BigDecimal matchingAmount;
    
    /** The matching codes. */
    private MatchingCodesDto matchingCodes;

    /**
     * Gets the matching amount.
     *
     * @return the matching amount
     */
    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    /**
     * Sets the matching amount.
     *
     * @param matchingAmount the new matching amount
     */
    public void setMatchingAmount(BigDecimal matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    /**
     * Gets the matching codes.
     *
     * @return the matching codes
     */
    public MatchingCodesDto getMatchingCodes() {
        return matchingCodes;
    }

    /**
     * Sets the matching codes.
     *
     * @param matchingCodes the new matching codes
     */
    public void setMatchingCodes(MatchingCodesDto matchingCodes) {
        this.matchingCodes = matchingCodes;
    }

    /**
     * Gets the matching code.
     *
     * @return the matching code
     */
    public String getMatchingCode() {
        return matchingCode;
    }

    /**
     * Sets the matching code.
     *
     * @param matchingCode the new matching code
     */
    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

}