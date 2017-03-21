package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminationReasonDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationReasonsDto implements Serializable {

	private static final long serialVersionUID = -2304338764451350201L;

	private List<TerminationReasonDto> terminationReason;

	public List<TerminationReasonDto> getTerminationReason() {
		if (terminationReason == null)
			terminationReason = new ArrayList<TerminationReasonDto>();
		return terminationReason;
	}

	public void setTerminationReason(List<TerminationReasonDto> terminationReason) {
		this.terminationReason = terminationReason;
	}

	@Override
	public String toString() {
		return "TerminationReasonsDto [terminationReason=" + terminationReason + "]";
	}

}
