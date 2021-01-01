package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.model.cpq.trade.CommercialRuleHeader;



/**
 * @author Rachid-Ait.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetCommercialRuleDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCommercialRuleDtoResponse extends CommercialRuleHeaderDTO{

    /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();
    
    private Boolean isTarget=Boolean.TRUE;

    /**
     * Instantiates a new base response.
     */
    public GetCommercialRuleDtoResponse() {
        actionStatus = new ActionStatus();
    }

    /**
     * Instantiates a new base response.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public GetCommercialRuleDtoResponse(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }

    
    public GetCommercialRuleDtoResponse(CommercialRuleHeader commercialRuleHeader) {
		super(commercialRuleHeader);
	}
    
	/**
	 * @return the actionStatus
	 */
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	/**
	 * @return the isTarget
	 */
	public Boolean getIsTarget() {
		return isTarget;
	}

	/**
	 * @param isTarget the isTarget to set
	 */
	public void setIsTarget(Boolean isTarget) {
		this.isTarget = isTarget;
	}


    
  
 
	
	
	
	 
	
	
	
}
