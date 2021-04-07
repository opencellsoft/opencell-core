package org.meveo.api.dto.cpq;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;

import io.swagger.v3.oas.annotations.media.Schema;

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
    @Schema(description = "code of offer template")
    private String offerTemplateCode;
    @Schema(description = "product associated to this offer product")
    private ProductDto product;
    
    /** Discount plans allowed for this product. */
    @XmlElementWrapper(name = "allowedDiscountPlans")
    @XmlElement(name = "allowedDiscountPlans")
    @Schema(description = "list of allowed discount plan")
    private List<DiscountPlanDto> allowedDiscountPlans;

    @Schema(description = "list of commercial rules")
    private List<CommercialRuleHeaderDTO> commercialRules=new ArrayList<CommercialRuleHeaderDTO>();

    @Schema(description = "indicate if offer product is selectable", defaultValue = "true")
    private boolean selectable=Boolean.TRUE;

    @Schema(description = "sequence")
    private Integer sequence=0;

    @Schema(description = "indicated if offer product is ruled", defaultValue = "false")
    private boolean ruled=Boolean.FALSE;

    @Schema(description = "indicate of the offer product is mandatory", defaultValue = "false")
	private boolean mandatory=Boolean.FALSE;

    @Schema(description = "indicate if the offer product can be displayed", defaultValue = "true")
	private boolean display = Boolean.TRUE;

    @Schema(description = "minimun quantity")
	private Integer quantityMin;

    @Schema(description = "maximun quantity")
	private Integer quantityMax;

    @Schema(description = "default quantity")
	private Integer quantityDefault;
    
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
	public List<CommercialRuleHeaderDTO> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(List<CommercialRuleHeaderDTO> commercialRules) {
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



	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}



	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}



	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}



	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}



	/**
	 * @return the quantityMin
	 */
	public Integer getQuantityMin() {
		return quantityMin;
	}



	/**
	 * @param quantityMin the quantityMin to set
	 */
	public void setQuantityMin(Integer quantityMin) {
		this.quantityMin = quantityMin;
	}



	/**
	 * @return the quantityMax
	 */
	public Integer getQuantityMax() {
		return quantityMax;
	}



	/**
	 * @param quantityMax the quantityMax to set
	 */
	public void setQuantityMax(Integer quantityMax) {
		this.quantityMax = quantityMax;
	}



	/**
	 * @return the quantityDefault
	 */
	public Integer getQuantityDefault() {
		return quantityDefault;
	}



	/**
	 * @param quantityDefault the quantityDefault to set
	 */
	public void setQuantityDefault(Integer quantityDefault) {
		this.quantityDefault = quantityDefault;
	}

	
   
}