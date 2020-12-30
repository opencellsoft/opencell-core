package org.meveo.api.dto.response.cpq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.CommercialRuleDTO;
import org.meveo.model.cpq.Attribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Mbarek-Ay.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetAttributeDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "chargeTemplateCodes"})
public class GetAttributeDtoResponse extends AttributeDTO{
 
	@XmlElementWrapper(name = "chargeTemplates")
    @XmlElement(name = "chargeTemplates")
    private Set<ChargeTemplateDto> chargeTemplates;
	
	
	@XmlElementWrapper(name = "commercialRules")
    @XmlElement(name = "commercialRules")
    private Set<CommercialRuleDTO> commercialRules;

	
	
    
 
 
 

    /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();

    /**
     * Instantiates a new base response.
     */
    public GetAttributeDtoResponse() {
        actionStatus = new ActionStatus();
    }

    /**
     * Instantiates a new base response.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public GetAttributeDtoResponse(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }
    
    public GetAttributeDtoResponse(Attribute attribute, Set<ChargeTemplateDto> chargeTemplates) {
 		super(attribute);
 		this.chargeTemplates = chargeTemplates;
 	}
    

    /**
     * Gets the action status.
     *
     * @return the action status
     */
    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    /**
     * Sets the action status.
     *
     * @param actionStatus the new action status
     */
    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }



	/**
	 * @return the chargeTemplates
	 */
	public Set<ChargeTemplateDto> getChargeTemplates() {
		return chargeTemplates;
	}

	/**
	 * @param chargeTemplates the chargeTemplates to set
	 */
	public void setChargeTemplates(Set<ChargeTemplateDto> chargeTemplates) {
		this.chargeTemplates = chargeTemplates;
	}

	/**
	 * @return the commercialRules
	 */
	public Set<CommercialRuleDTO> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(Set<CommercialRuleDTO> commercialRules) {
		this.commercialRules = commercialRules;
	}


    
  
 
	
	
	
	 
	
	
	
}
