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
import org.meveo.model.cpq.offer.OfferComponent;

/**
 * 
 * @author Tarik FAKHOURI
 * @author Mbarek-Ay
 * @version 10.0
 */ 
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductsDto extends BaseEntityDto {
    
     /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7824004884683019697L;  
    private String prodcutCode;
    @NotNull
    private String offerTemplateCode;
    
    /** The product dto. */
    private ProductDto product;
    
    /** The prerequisite Products */
    @XmlElementWrapper(name = "prerequisiteProducts")
    @XmlElement(name = "prerequisiteProducts")
    private List<ProductDto> prerequisiteProducts= new ArrayList<>();
    
    /** The incompatible Products. */
    @XmlElementWrapper(name = "incompatibleProducts")
    @XmlElement(name = "incompatibleProducts")
    private List<ProductDto> incompatibleProducts= new ArrayList<>();
    
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tagList = new HashSet<>();
    
    /**
     * Instantiates a new Offer Component dto.
     */
    public OfferProductsDto() {
    }
    
    public OfferProductsDto(OfferComponent o) {  
    	 if (o.getProduct() != null) {
    		 product = new ProductDto(o.getProduct());
         } 
       if(o.getTagsList() != null && !o.getTagsList().isEmpty()) {
    	   o.getTagsList().forEach(t -> {
    		   tagList.add(new TagDto(t));
    	   });
       }
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

	/**
	 * @return the prodcutCode
	 */
	public String getProdcutCode() {
		return prodcutCode;
	}

	/**
	 * @param prodcutCode the prodcutCode to set
	 */
	public void setProdcutCode(String prodcutCode) {
		this.prodcutCode = prodcutCode;
	}
	public List<ProductDto> getIncompatibleProducts() {
		return incompatibleProducts;
	}

	/**
	 * @param incompatibleProducts the incompatibleProducts to set
	 */
	public void setIncompatibleProducts(List<ProductDto> incompatibleProducts) {
		this.incompatibleProducts = incompatibleProducts;
	}
   
}