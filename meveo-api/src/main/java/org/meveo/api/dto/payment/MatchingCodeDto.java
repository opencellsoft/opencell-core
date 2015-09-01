package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "MatchingCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingCodeDto implements Serializable {

	private static final long serialVersionUID = 5657981714421497476L;

	private String code;
	private String matchingType;
	private Date matchingDate;
	private BigDecimal matchingAmountCredit;
	private BigDecimal matchingAmountDebit;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(String matchingType) {
		this.matchingType = matchingType;
	}

	public Date getMatchingDate() {
		return matchingDate;
	}

	public void setMatchingDate(Date matchingDate) {
		this.matchingDate = matchingDate;
	}

	public BigDecimal getMatchingAmountCredit() {
		return matchingAmountCredit;
	}

	public void setMatchingAmountCredit(BigDecimal matchingAmountCredit) {
		this.matchingAmountCredit = matchingAmountCredit;
	}

	public BigDecimal getMatchingAmountDebit() {
		return matchingAmountDebit;
	}

	public void setMatchingAmountDebit(BigDecimal matchingAmountDebit) {
		this.matchingAmountDebit = matchingAmountDebit;
	}

}
