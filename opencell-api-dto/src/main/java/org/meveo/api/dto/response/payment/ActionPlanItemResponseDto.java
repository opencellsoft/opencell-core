package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name="ActionPlanItemResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionPlanItemResponseDto extends BaseResponse {

	private static final long serialVersionUID = -1865272131750462531L;
	private WFActionDto actionPlanItem;

	public void setActionPlanItem(WFActionDto actionPlanItem) {
		this.actionPlanItem = actionPlanItem;
	}
	
	public WFActionDto getActionPlanItem() {
		return actionPlanItem;
	}
	
	@Override
	public String toString() {
		return "ActionPlanItemDto [ActionPlanItemDto=" + actionPlanItem + "]";
	}
}

