package org.meveo.api.dto.response.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "AccountOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDtosResponse extends BaseResponse {

	private static final long serialVersionUID = 4142972198689221570L;

	@XmlElementWrapper(name = "revenueRecognitionRules")
    @XmlElement(name = "revenueRecognitionRule")
    private List<RevenueRecognitionRuleDto> revenueRecognitionRules;
	
    
	public List<RevenueRecognitionRuleDto> getRevenueRecognitionRules() {
		return revenueRecognitionRules;
	}

	public void setRevenueRecognitionRules(List<RevenueRecognitionRuleDto> revenueRecognitionRules) {
		this.revenueRecognitionRules = revenueRecognitionRules;
	}

	@Override
	public String toString() {
		return "RevenueRecognitionRuleDtosResponse [revenueRecognitionRules=" + revenueRecognitionRules + "]";
	}
}
