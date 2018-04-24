package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.MatchingTypeEnum;

/**
 * The Class MatchingCodeDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MatchingCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingCodeDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5657981714421497476L;

    /** The code. */
    private String code;
    
    /** The matching type. */
    private MatchingTypeEnum matchingType;
    
    /** The matching date. */
    private Date matchingDate;
    
    /** The matching amount credit. */
    private BigDecimal matchingAmountCredit;
    
    /** The matching amount debit. */
    private BigDecimal matchingAmountDebit;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the matching type.
     *
     * @return the matching type
     */
    public MatchingTypeEnum getMatchingType() {
        return matchingType;
    }

    /**
     * Sets the matching type.
     *
     * @param matchingType the new matching type
     */
    public void setMatchingType(MatchingTypeEnum matchingType) {
        this.matchingType = matchingType;
    }

    /**
     * Gets the matching date.
     *
     * @return the matching date
     */
    public Date getMatchingDate() {
        return matchingDate;
    }

    /**
     * Sets the matching date.
     *
     * @param matchingDate the new matching date
     */
    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    /**
     * Gets the matching amount credit.
     *
     * @return the matching amount credit
     */
    public BigDecimal getMatchingAmountCredit() {
        return matchingAmountCredit;
    }

    /**
     * Sets the matching amount credit.
     *
     * @param matchingAmountCredit the new matching amount credit
     */
    public void setMatchingAmountCredit(BigDecimal matchingAmountCredit) {
        this.matchingAmountCredit = matchingAmountCredit;
    }

    /**
     * Gets the matching amount debit.
     *
     * @return the matching amount debit
     */
    public BigDecimal getMatchingAmountDebit() {
        return matchingAmountDebit;
    }

    /**
     * Sets the matching amount debit.
     *
     * @param matchingAmountDebit the new matching amount debit
     */
    public void setMatchingAmountDebit(BigDecimal matchingAmountDebit) {
        this.matchingAmountDebit = matchingAmountDebit;
    }

}