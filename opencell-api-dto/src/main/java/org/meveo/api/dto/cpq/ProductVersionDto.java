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
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
/**
 * 
 * @author Mbarek-Ay
 * @author Rachid.AITYAAZZA
 * @version 11.0
 */ 

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductVersionDto extends BaseEntityDto {
    
     /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7824004884683019697L;  
    /** The shortDescription. */
    @XmlAttribute()
    @NotNull
    private String shortDescription;
    /** The product code. */
    @NotNull
    @XmlElement(required = true)
    private String productCode;
    
    @NotNull
    /** The currentVersion. */
    private int currentVersion;
    /** The status. */
    private VersionStatusEnum status;
    /** The statusDate. */
    private Date statusDate;
    /** The longDescription */
    private String longDescription ;
    /**The validity Date*/
    private DatePeriod validity = new DatePeriod();
 
    /** The services template. */
    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attributes")
    private Set<AttributeDTO> attributes=new HashSet<>();;
    
    @XmlElementWrapper(name = "groupedAttributes")
    @XmlElement(name = "groupedAttributes")
    private Set<GroupedAttributeDto> groupedAttributes = new HashSet<>();
    
    /** The services template. */
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tagList = new HashSet<>();
    

    
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
        this.shortDescription = productVersion.getShortDescription();
        this.productCode = productVersion.getProduct().getCode();
        this.currentVersion = productVersion.getCurrentVersion();
        this.status = productVersion.getStatus();
        this.statusDate = productVersion.getStatusDate();
        this.longDescription =productVersion.getLongDescription();
        this.validity = productVersion.getValidity(); 
 
    }
    public ProductVersionDto(ProductVersion productVersion,boolean loadAttributes,boolean loadTags) {
    	
        this.shortDescription = productVersion.getShortDescription();
        this.productCode = productVersion.getProduct().getCode();
        this.currentVersion = productVersion.getCurrentVersion();
        this.status = productVersion.getStatus();
        this.statusDate = productVersion.getStatusDate();
        this.longDescription =productVersion.getLongDescription();
        this.validity = productVersion.getValidity(); 
        
    	if(loadAttributes) {
    		if(productVersion.getAttributes() != null && !productVersion.getAttributes().isEmpty()) {
    			attributes = productVersion.getAttributes().stream().map(d -> {
    				final AttributeDTO attributeDto = new AttributeDTO(d);
    				return attributeDto;
    			}).collect(Collectors.toSet());
    		}
    		if(productVersion.getGroupedAttributes() != null && !productVersion.getGroupedAttributes().isEmpty()) {
    			groupedAttributes = productVersion.getGroupedAttributes().stream().map(d -> {
    				final GroupedAttributeDto groupedAttributesDto = new GroupedAttributeDto(d);
    				return groupedAttributesDto;
    			}).collect(Collectors.toSet());
    		}

    	}
    	if(loadTags) { 
    		if(productVersion.getTags() != null && !productVersion.getTags().isEmpty()) {
    			tagList = productVersion.getTags().stream().map(t -> {
    				final TagDto dto = new TagDto(t);
    				return dto;
    			}).collect(Collectors.toSet());
    		} 
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
	 * @return the services
	 */
	public Set<AttributeDTO> getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the services to set
	 */
	public void setAttributes(Set<AttributeDTO> attributes) {
		this.attributes = attributes;
	}
	@Override
    public String toString() {
        return "ProductVersionDto [shortDescription=" + shortDescription + ", productCode=" + productCode
                + ", currentVersion=" + currentVersion + ", status=" + status + ", statusDate=" + statusDate
                + ", longDescription=" + longDescription + ", validity=" + validity + "]";
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
	 * @return the groupedAttributes
	 */
	public Set<GroupedAttributeDto> getGroupedAttributes() {
		return groupedAttributes;
	}
	/**
	 * @param groupedAttributes the groupedAttributes to set
	 */
	public void setGroupedAttributes(Set<GroupedAttributeDto> groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
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
     
	
    
}