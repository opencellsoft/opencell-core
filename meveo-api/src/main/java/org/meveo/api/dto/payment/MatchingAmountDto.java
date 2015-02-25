package org.meveo.api.dto.payment;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MatchingAmount")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountDto {

	private String matchingCode;
	private BigDecimal matchingAmount;
	private MatchingCodesDto matchingCodes;

	public BigDecimal getMatchingAmount() {
		return matchingAmount;
	}

	public void setMatchingAmount(BigDecimal matchingAmount) {
		this.matchingAmount = matchingAmount;
	}

	public MatchingCodesDto getMatchingCodes() {
		return matchingCodes;
	}

	public void setMatchingCodes(MatchingCodesDto matchingCodes) {
		this.matchingCodes = matchingCodes;
	}

	public String getMatchingCode() {
		return matchingCode;
	}

	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}

}
