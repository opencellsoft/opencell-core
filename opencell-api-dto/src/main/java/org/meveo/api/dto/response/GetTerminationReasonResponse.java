package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TerminationReasonDto;

@XmlRootElement(name = "TerminationReasonResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTerminationReasonResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private List<TerminationReasonDto> terminationReason = new ArrayList<TerminationReasonDto>();

	public List<TerminationReasonDto> getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(List<TerminationReasonDto> terminationReason) {
		this.terminationReason = terminationReason;
	}

	@Override
	public String toString() {
		return "GetTerminationReasonResponse [terminationReason="
				+ terminationReason + "]";
	}

}
