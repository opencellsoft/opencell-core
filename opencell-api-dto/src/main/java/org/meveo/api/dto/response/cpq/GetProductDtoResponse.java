package org.meveo.api.dto.response.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.cpq.Product;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetProductDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductDtoResponse extends BaseResponse{

	private ProductDto productDto;
	
	private Set<DiscountPlanDto> discountList = new HashSet<>();
    private Set<ProductVersionDto> productVersions = new HashSet<>();
    
    
    public GetProductDtoResponse(Product p) {
    	super();
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		productDto=new ProductDto(p);
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
	 * @return the productDto
	 */
	public ProductDto getProductDto() {
		return productDto;
	}

	/**
	 * @param productDto the productDto to set
	 */
	public void setProductDto(ProductDto productDto) {
		this.productDto = productDto;
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
	
	
}
