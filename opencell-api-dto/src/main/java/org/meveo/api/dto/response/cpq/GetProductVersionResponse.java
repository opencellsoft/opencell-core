package org.meveo.api.dto.response.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.cpq.ServiceDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.cpq.ProductVersion;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductVersionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductVersionResponse extends BaseResponse{

	private ProductVersionDto productVersionDto;

    private Set<ServiceDTO> serviceList = new HashSet<>();
    
    private Set<TagDto> tagList = new HashSet<>();

	public GetProductVersionResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}
	
	public GetProductVersionResponse(ProductVersion productVersion) {
		this();
	}

	/**
	 * @return the productVersionDto
	 */
	public ProductVersionDto getProductVersionDto() {
		return productVersionDto;
	}

	/**
	 * @param productVersionDto the productVersionDto to set
	 */
	public void setProductVersionDto(ProductVersionDto productVersionDto) {
		this.productVersionDto = productVersionDto;
	}


	/**
	 * @return the serviceList
	 */
	public Set<ServiceDTO> getServiceList() {
		return serviceList;
	}

	/**
	 * @param serviceList the serviceList to set
	 */
	public void setServiceList(Set<ServiceDTO> serviceList) {
		this.serviceList = serviceList;
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

	
}
