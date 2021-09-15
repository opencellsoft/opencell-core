package org.meveo.api.dto.cpq;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 
 * @author Mbarek-Ay
 * @author Rachid.AITYAAZZA
 * @version 11.0
 */ 

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductVersionDto extends BaseEntityDto {
    
     /** The Constant serialVersionUID. */
    protected static final long serialVersionUID = -7824004884683019697L;  
    /** The shortDescription. */
    @XmlAttribute()
    @NotNull
    @Schema(description = "The short description of the product version", required = true)
    protected String shortDescription;
    /** The product code. */
    @NotNull
    @XmlElement(required = true)
    @Schema(description = "The product code", required = true)
    protected String productCode;
    
    @NotNull
    /** The currentVersion. */
    @Schema(description = "The current version of the product")
    protected int currentVersion;
    /** The status. */
    @Schema(description = "The status of the product version")
    protected VersionStatusEnum status;
    /** The statusDate. */
    @Schema(description = "The statusDate : is set automatically when the status was changed")
    protected Date statusDate;
    /** The longDescription */
    @Schema(description = "The long description")
    protected String longDescription ;
    /**The validity Date*/
    @Schema(description = "The validity Date")
    protected DatePeriod validity = new DatePeriod();
 
    /** The attributeCodes. */
    @XmlElementWrapper(name = "attributeCodes")
    @XmlElement(name = "attributeCodes")
    @Schema(description = "List of the attribute")
    protected Set<ProductVersionAttributeDTO> attributes=new HashSet<ProductVersionAttributeDTO>();
    
    @XmlElementWrapper(name = "groupedAttributeCodes")
    @XmlElement(name = "groupedAttributeCodes")
    @Schema(description = "List of the grouped attribute codes")
    protected Set<String> groupedAttributeCodes = new HashSet<String>();
    
    /** The services template. */
    @XmlElementWrapper(name = "tagCodes")
    @XmlElement(name = "tagCodes")
    @Schema(description = "List of tag codes")
    protected Set<String> tagCodes = new HashSet<String>();
    

    
    /**
     * Instantiates a new product version dto.
     */
    public ProductVersionDto() {
    }
    /**
     * Instantiates a new product dto.
     *
     * @param productChargeInstance the ProductChargeInstance entity
     * @param customFieldInstances the custom field instances
     */
 
    
    public ProductVersionDto(ProductVersion productVersion) {  
       super();
       init(productVersion);
 
    }
    public void init(ProductVersion productVersion) { 
    	 this.shortDescription = productVersion.getShortDescription();
         this.productCode = productVersion.getProduct().getCode();
         this.currentVersion = productVersion.getCurrentVersion();
         this.status = productVersion.getStatus();
         this.statusDate = productVersion.getStatusDate();
         this.longDescription =productVersion.getLongDescription();
         this.validity = productVersion.getValidity();
         if(productVersion.getTags() != null && !productVersion.getTags().isEmpty()) {
         this.tagCodes = productVersion.getTags()
                 .stream()
                 .map(tag -> tag.getCode())
                 .collect(Collectors.toSet());
         }
         if(productVersion.getAttributes() != null && !productVersion.getAttributes().isEmpty()) {
        	 this.attributes = productVersion.getAttributes()
                     .stream()
                     .map(ProductVersionAttributeDTO::new)
                     .collect(Collectors.toSet());
         }
         if(productVersion.getGroupedAttributes() != null && !productVersion.getGroupedAttributes().isEmpty()) {
        	 this.attributes = productVersion.getAttributes()
                     .stream()
                     .map(ProductVersionAttributeDTO::new)
                     .collect(Collectors.toSet());
         }
    }
   
    
    
    
    /**
     * @return the shortDescription
     */
    public String getShortDescription() {
        return shortDescription;
    }
    /**
     * @param shortDescription the shortDescription to set
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    /**
     * @return the productCode
     */
    public String getProductCode() {
        return productCode;
    }
    /**
     * @param productCode the productCode to set
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    /**
     * @return the currentVersion
     */
    public int getCurrentVersion() {
        return currentVersion;
    }
    /**
     * @param currentVersion the currentVersion to set
     */
    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }
    /**
     * @return the status
     */
    public VersionStatusEnum getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(VersionStatusEnum status) {
        this.status = status;
    }
    /**
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }
    /**
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
    /**
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }
    /**
     * @param longDescription the longDescription to set
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
	/**
	 * @return the groupedAttributeCodes
	 */
	public Set<String> getGroupedAttributeCodes() {
		return groupedAttributeCodes;
	}
	/**
	 * @param groupedAttributeCodes the groupedAttributeCodes to set
	 */
	public void setGroupedAttributeCodes(Set<String> groupedAttributeCodes) {
		this.groupedAttributeCodes = groupedAttributeCodes;
	}
	/**
	 * @return the tagCodes
	 */
	public Set<String> getTagCodes() {
		return tagCodes;
	}
	/**
	 * @param tagCodes the tagCodes to set
	 */
	public void setTagCodes(Set<String> tagCodes) {
		this.tagCodes = tagCodes;
	}
	@Override
    public String toString() {
        return "ProductVersionDto [shortDescription=" + shortDescription + ", productCode=" + productCode
                + ", currentVersion=" + currentVersion + ", status=" + status + ", statusDate=" + statusDate
                + ", longDescription=" + longDescription + ", validity=" + validity + "]";
    }
	 
	/**
	 * @return the validity
	 */
	public DatePeriod getValidity() {
		return validity;
	}
	/**
	 * @param validity the validity to set
	 */
	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}

    /**
     * @return the attributes
     */
    public Set<ProductVersionAttributeDTO> getAttributes() {
        return attributes;
    }
    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Set<ProductVersionAttributeDTO> attributes) {
        this.attributes = attributes;
    }
	
    
}