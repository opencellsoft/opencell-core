package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:08:59 AM
 *
 */
@XmlRootElement(name="DunningPlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningPlanResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2492883573757679482L;
	public DunningPlanDto getDunningPlan() {
		return dunningPlan;
	}
	public void setDunningPlan(DunningPlanDto dunningPlan) {
		this.dunningPlan = dunningPlan;
	}
	private DunningPlanDto dunningPlan;
}

