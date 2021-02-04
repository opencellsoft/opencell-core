package org.meveo.api.dto.response.cpq;

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
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.model.cpq.Attribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Mbarek-Ay.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetAttributeDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "chargeTemplateCodes","commercialRuleCodes","tagCodes","assignedAttributeCodes"})
public class GetAttributeDtoResponse extends AttributeDTO{
 
	@XmlElementWrapper(name = "chargeTemplates")
    @XmlElement(name = "chargeTemplates")
    private Set<ChargeTemplateDto> chargeTemplates;
	
	
	@XmlElementWrapper(name = "commercialRules")
    @XmlElement(name = "commercialRules")
    private Set<CommercialRuleHeaderDTO> commercialRules;
	
	@XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private List<TagDto> tags;
	
	@XmlElementWrapper(name = "assignedAttributes")
    @XmlElement(name = "assignedAttributes")
    private List<AttributeDTO> assignedAttributes;


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
    
    public GetAttributeDtoResponse(Attribute attribute, Set<ChargeTemplateDto> chargeTemplates, List<TagDto> tags,List<AttributeDTO> assignedAttributes) {
 		super(attribute);
 		this.chargeTemplates = chargeTemplates;
 		this.tags=tags;
 		this.assignedAttributes=assignedAttributes;
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
	public Set<CommercialRuleHeaderDTO> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(Set<CommercialRuleHeaderDTO> commercialRules) {
		this.commercialRules = commercialRules;
	}

	/**
	 * @return the tags
	 */
	public List<TagDto> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}

	/**
	 * @return the assignedAttributes
	 */
	public List<AttributeDTO> getAssignedAttributes() {
		return assignedAttributes;
	}

	/**
	 * @param assignedAttributes the assignedAttributes to set
	 */
	public void setAssignedAttributes(List<AttributeDTO> assignedAttributes) {
		this.assignedAttributes = assignedAttributes;
	}
	
	





    
  
 
	
	
	
	 
	
	
	
}
