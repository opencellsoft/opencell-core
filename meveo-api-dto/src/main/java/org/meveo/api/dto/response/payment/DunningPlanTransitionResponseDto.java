package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:08:59 AM
 *
 */
@XmlRootElement(name="DunningPlanTransitionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningPlanTransitionResponseDto extends BaseResponse {

	private static final long serialVersionUID = -9076373795496333905L;
	private DunningPlanTransitionDto dunningPlanTransition;

	public DunningPlanTransitionDto getDunningPlanTransition() {
		return dunningPlanTransition;
	}
	
	public void setDunningPlanTransition(DunningPlanTransitionDto dunningPlanTransition) {
		this.dunningPlanTransition = dunningPlanTransition;
	}
	
	@Override
	public String toString() {
		return "DunningPlanTransitionDto [DunningPlanTransitionDto=" + dunningPlanTransition + "]";
	}
}

