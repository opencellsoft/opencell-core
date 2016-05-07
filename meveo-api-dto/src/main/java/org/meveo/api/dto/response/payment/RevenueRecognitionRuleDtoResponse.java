package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "AccountOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDtoResponse extends BaseResponse {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2160634983966387377L;
	

	private RevenueRecognitionRuleDto revenueRecognitionRuleDto;


	public RevenueRecognitionRuleDto getRevenueRecognitionRuleDto() {
		return revenueRecognitionRuleDto;
	}


	public void setRevenueRecognitionRuleDto(RevenueRecognitionRuleDto revenueRecognitionRuleDto) {
		this.revenueRecognitionRuleDto = revenueRecognitionRuleDto;
	}


	@Override
	public String toString() {
		return "RevenueRecognitionRuleDtoResponse [revenueRecognitionRule=" + revenueRecognitionRuleDto + "]";
	}
}
