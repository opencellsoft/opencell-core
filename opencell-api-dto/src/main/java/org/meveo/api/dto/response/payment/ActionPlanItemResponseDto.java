package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ActionPlanItemResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 */
@XmlRootElement(name = "ActionPlanItemResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionPlanItemResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1865272131750462531L;
    
    /** The action plan item. */
    private WFActionDto actionPlanItem;

    /**
     * Sets the action plan item.
     *
     * @param actionPlanItem the new action plan item
     */
    public void setActionPlanItem(WFActionDto actionPlanItem) {
        this.actionPlanItem = actionPlanItem;
    }

    /**
     * Gets the action plan item.
     *
     * @return the action plan item
     */
    public WFActionDto getActionPlanItem() {
        return actionPlanItem;
    }

    @Override
    public String toString() {
        return "ActionPlanItemDto [ActionPlanItemDto=" + actionPlanItem + "]";
    }
}