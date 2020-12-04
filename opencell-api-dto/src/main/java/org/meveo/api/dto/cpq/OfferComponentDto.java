package org.meveo.api.dto.cpq;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.model.cpq.offer.OfferComponent;

/**
 * 
 * @author Tarik FAKHOURI
 * @version 10.0
 */ 
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferComponentDto extends BaseEntityDto {
    
     /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7824004884683019697L;  
    
    private String offerTemplateCode;
    
    /** The product dto. */
    private ProductDto product;
    
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tagList = new HashSet<>();
    
    /**
     * Instantiates a new Offer Component dto.
     */
    public OfferComponentDto() {
    }
    
    public OfferComponentDto(OfferComponent o) {  
    	 if (o.getProduct() != null) {
    		 product = new ProductDto(o.getProduct());
         }
       this.offerTemplateCode = o.getCommercialOffer().getCode();
       if(o.getTagsList() != null && !o.getTagsList().isEmpty()) {
    	   o.getTagsList().forEach(t -> {
    		   tagList.add(new TagDto(t));
    	   });
       }
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
	 * @return the tagList
	 */
	public Set<TagDto> getTagList() {
		return tagList;
	}

	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(Set<TagDto> tagList) {
		this.tagList = tagList;
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
   
	
     
    
}