package org.meveo.api.dto.response.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.model.cpq.Product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductDtoResponse")
@JsonIgnoreProperties({ "chargeTemplateCodes","commercialRuleCodes"})
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductDtoResponse extends ProductDto{
	
	private Set<DiscountPlanDto> discountList = new HashSet<>();
    private Set<ProductVersionDto> productVersions = new HashSet<>();
    @XmlElementWrapper(name = "chargeTemplates")
    @XmlElement(name = "chargeTemplates")
    private Set<ChargeTemplateDto> chargeTemplates;
    
	@XmlElementWrapper(name = "commercialRules")
    @XmlElement(name = "commercialRules")
    private Set<CommercialRuleHeaderDTO> commercialHeaderRules;
	
    /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();
    
    public GetProductDtoResponse(Product p) {
    	super(p);
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
    	if(p.getDiscountList() != null && !p.getDiscountList().isEmpty()) {
    		discountList = p.getDiscountList().stream().map(d -> {
    			final DiscountPlanDto discount = new DiscountPlanDto(d, null);
    			return discount;
    		}).collect(Collectors.toSet());
    	}
    	
    	if(p.getProductVersions() != null && !p.getProductVersions().isEmpty()) {
    		productVersions = p.getProductVersions().stream().map(d -> {
    			final ProductVersionDto service = new ProductVersionDto(d);
    			return service;
    		}).collect(Collectors.toSet());
    	}
    	
    }
	
	public GetProductDtoResponse() {
		super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}


	/**
	 * @return the productVersions
	 */
	public Set<ProductVersionDto> getProductVersions() {
		return productVersions;
	}

	/**
	 * @param productVersions the productVersions to set
	 */
	public void setProductVersions(Set<ProductVersionDto> productVersions) {
		this.productVersions = productVersions;
	}

	/**
	 * @return the discountList
	 */
	public Set<DiscountPlanDto> getDiscountList() {
		return discountList;
	}

	/**
	 * @param discountList the discountList to set
	 */
	public void setDiscountList(Set<DiscountPlanDto> discountList) {
		this.discountList = discountList;
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
	 * @return the commercialHeaderRules
	 */
	public Set<CommercialRuleHeaderDTO> getCommercialHeaderRules() {
		return commercialHeaderRules;
	}

	/**
	 * @param commercialHeaderRules the commercialHeaderRules to set
	 */
	public void setCommercialHeaderRules(Set<CommercialRuleHeaderDTO> commercialHeaderRules) {
		this.commercialHeaderRules = commercialHeaderRules;
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
	
	
	
}
