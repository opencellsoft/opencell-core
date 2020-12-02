package org.meveo.api.dto.response.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.cpq.AttributeDTO;
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



	
}
