package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "matchingAmount")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountDto {

	private String matchingCode;
	private BigDecimal matchingAmount;

	public String getMatchingCode() {
		return matchingCode;
	}

	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}

	public BigDecimal getMatchingAmount() {
		return matchingAmount;
	}

	public void setMatchingAmount(BigDecimal matchingAmount) {
		this.matchingAmount = matchingAmount;
	}

}
