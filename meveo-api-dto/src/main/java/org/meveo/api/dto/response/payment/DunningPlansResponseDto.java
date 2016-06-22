package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DunningPlansDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:11:07 AM
 *
 */
@XmlRootElement(name="DunningPlansResponse")
public class DunningPlansResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1262341691039525086L;
	private DunningPlansDto dunningPlans;
	public DunningPlansDto getDunningPlans() {
		return dunningPlans;
	}
	public void setDunningPlans(DunningPlansDto dunningPlans) {
		this.dunningPlans = dunningPlans;
	}
	
}

