package org.meveo.api.dto.response.cpq;

import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.model.cpq.ProductVersion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Tarik F.
 * @author Mbarek-Ay
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductVersionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "attributeCodes","groupedAttributeCodes","tagCodes"})
public class GetProductVersionResponse extends ProductVersionDto{ 
	
	
	@XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attributes")
    private Set<AttributeDTO> attributes;
 
	
	@XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tagList;
	
    @XmlElementWrapper(name = "groupedAttributes")
    @XmlElement(name = "groupedAttributes")
    private Set<GroupedAttributeDto> groupedAttributes;
    
	
	public GetProductVersionResponse() { 
	}
	
	public GetProductVersionResponse(ProductVersion productVersion) {
		super(productVersion);
	    this.shortDescription = productVersion.getShortDescription();
        this.productCode = productVersion.getProduct().getCode();
        this.currentVersion = productVersion.getCurrentVersion();
        this.status = productVersion.getStatus();
        this.statusDate = productVersion.getStatusDate();
        this.longDescription =productVersion.getLongDescription();
        this.validity = productVersion.getValidity(); 
	}
	
	
	 public GetProductVersionResponse(ProductVersion productVersion,boolean loadAttributes,boolean loadTags) {
 
		 super();
		 init(productVersion);

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
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();

 
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
	 * @return the attributes
	 */
	public Set<AttributeDTO> getAttributes() {
		return attributes;
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
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Set<AttributeDTO> attributes) {
		this.attributes = attributes;
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

 
	
	
	

	
    
    

	 


	
}
