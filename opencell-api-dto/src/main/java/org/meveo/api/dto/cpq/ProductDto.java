package org.meveo.api.dto.cpq;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

@XmlRootElement(name = "ProductDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto {

	private Long id;
	private String code;
	private String label;
	private ProductStatusEnum status;
	private Date statusDate;
	private ProductLineDto productLine;
	private CustomerBrandDto brand;
	private String reference;
	private String model;
	private Set<String> modelChlidren;
	private Set<DiscountPlanDto> discountList = new HashSet<>();
	private boolean discountFlag;
    private boolean packageFlag;
    
    
    public ProductDto() {}
    
    public ProductDto(Product p) {
    	this.id = p.getId();
    	this.code = p.getCode();
    	this.label = p.getDescription();
    	this.status = p.getStatus();
    	this.statusDate = p.getStatusDate();
    	if(p.getProductLine() != null) {
        	this.productLine = new ProductLineDto(p.getProductLine());
    	}
    	if(p.getBrand() != null) {
    		this.brand = new CustomerBrandDto(p.getBrand());
    	}
    	this.reference = p.getReference();
    	this.model = p.getModel();
    	this.modelChlidren = p.getModelChlidren();
    	if(p.getDiscountList() != null && !p.getDiscountList().isEmpty()) {
    		discountList = p.getDiscountList().stream().map(d -> {
    			final DiscountPlanDto discount = new DiscountPlanDto(d, null);
    			return discount;
    		}).collect(Collectors.toSet());
    	}
    	this.discountFlag = p.isDiscountFlag();
    	this.packageFlag = p.isPackageFlag();
    }
	/**
	 * @return the status
	 */
	public ProductStatusEnum getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(ProductStatusEnum status) {
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
	 * @return the productLine
	 */
	public ProductLineDto getProductLine() {
		return productLine;
	}
	/**
	 * @param productLine the productLine to set
	 */
	public void setProductLine(ProductLineDto productLine) {
		this.productLine = productLine;
	}
	/**
	 * @return the brand
	 */
	public CustomerBrandDto getBrand() {
		return brand;
	}
	/**
	 * @param brand the brand to set
	 */
	public void setBrand(CustomerBrandDto brand) {
		this.brand = brand;
	}
	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the modelChlidren
	 */
	public Set<String> getModelChlidren() {
		return modelChlidren;
	}
	/**
	 * @param modelChlidren the modelChlidren to set
	 */
	public void setModelChlidren(Set<String> modelChlidren) {
		this.modelChlidren = modelChlidren;
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
	/**
	 * @return the discountFlag
	 */
	public boolean isDiscountFlag() {
		return discountFlag;
	}
	/**
	 * @param discountFlag the discountFlag to set
	 */
	public void setDiscountFlag(boolean discountFlag) {
		this.discountFlag = discountFlag;
	}
	/**
	 * @return the packageFlag
	 */
	public boolean isPackageFlag() {
		return packageFlag;
	}
	/**
	 * @param packageFlag the packageFlag to set
	 */
	public void setPackageFlag(boolean packageFlag) {
		this.packageFlag = packageFlag;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
    
    
}
