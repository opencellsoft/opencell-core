package org.meveo.api.dto.cpq;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.model.cpq.offer.OfferComponent;

/**
 * 
 * @author Tarik FAKHOURI
 * @author Mbarek-Ay
 * @version 11.0
 */ 
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductsDto extends BaseEntityDto {
    
     /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7824004884683019697L;  
    @NotNull
    private String offerTemplateCode;
    private ProductDto product;
    
    /** Discount plans allowed for this product. */
    @XmlElementWrapper(name = "allowedDiscountPlans")
    @XmlElement(name = "allowedDiscountPlans")
    private List<DiscountPlanDto> allowedDiscountPlans;
    
    private List<CommercialRuleDTO> commercialRules=new ArrayList<CommercialRuleDTO>();

    private boolean selectable=Boolean.TRUE;
    
    private Integer sequence=0;
    
    private boolean ruled=Boolean.FALSE;
    
    /**
     * Instantiates a new Offer Component dto.
     */
    public OfferProductsDto() {
    }
   


	/**
	 * @return the allowedDiscountPlans
	 */
	public List<DiscountPlanDto> getAllowedDiscountPlans() {
		return allowedDiscountPlans;
	}

	/**
	 * @param allowedDiscountPlans the allowedDiscountPlans to set
	 */
	public void setAllowedDiscountPlans(List<DiscountPlanDto> allowedDiscountPlans) {
		this.allowedDiscountPlans = allowedDiscountPlans;
	}

	/**
	 * @return the offerTemplateCode
	 */
	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	/**
	 * @param offerTemplateCode the offerTemplateCode to set
	 */
	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}

	/**
	 * @return the commercialRules
	 */
	public List<CommercialRuleDTO> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(List<CommercialRuleDTO> commercialRules) {
		this.commercialRules = commercialRules;
	}






	/**
	 * @return the product
	 */
	public ProductDto getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(ProductDto product) {
		this.product = product;
	}

	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the ruled
	 */
	public boolean isRuled() {
		return ruled;
	}

	/**
	 * @param ruled the ruled to set
	 */
	public void setRuled(boolean ruled) {
		this.ruled = ruled;
	}

	
   
}