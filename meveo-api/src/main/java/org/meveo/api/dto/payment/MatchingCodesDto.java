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
@XmlRootElement(name = "MatchingCodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingCodesDto implements Serializable {

	private static final long serialVersionUID = 5230851336194617883L;

	private List<MatchingCodeDto> matchingCode;

	public List<MatchingCodeDto> getMatchingCode() {
		if (matchingCode == null)
			matchingCode = new ArrayList<MatchingCodeDto>();
		return matchingCode;
	}

	public void setMatchingCode(List<MatchingCodeDto> matchingCode) {
		this.matchingCode = matchingCode;
	}

}
