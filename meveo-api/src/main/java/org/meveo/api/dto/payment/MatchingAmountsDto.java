package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "MatchingAmounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountsDto implements Serializable {

	private static final long serialVersionUID = -8375302840183850003L;

	private List<MatchingAmountDto> matchingAmount;

	public List<MatchingAmountDto> getMatchingAmount() {
		if (matchingAmount == null)
			matchingAmount = new ArrayList<MatchingAmountDto>();
		return matchingAmount;
	}

	public void setMatchingAmount(List<MatchingAmountDto> matchingAmount) {
		this.matchingAmount = matchingAmount;
	}

}
