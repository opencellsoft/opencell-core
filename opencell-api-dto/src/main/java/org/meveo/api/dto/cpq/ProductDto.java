package org.meveo.api.dto.cpq;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.ProductStatusEnum;

@XmlRootElement(name = "CpqProductDto")
@XmlType(name = "CpqProductDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto extends BaseEntityDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2483466298983716926L;
    @NotNull
	private String code;
	private String label;
	private ProductStatusEnum status;
	private Date statusDate;
	private String productLineCode;
	private String brandCode;
	private String reference;
	private String model;
	private Set<String> modelChildren;
	private boolean discountFlag=Boolean.FALSE;
    private boolean packageFlag=Boolean.FALSE;
    /** The custom fields. */
    private CustomFieldsDto customFields;
    private ProductVersionDto currentProductVersion;
    
   

    
    
    public ProductDto() {}
    
    public ProductDto(Product p) {
    	this.code = p.getCode();
    	this.label = p.getDescription();
    	this.status = p.getStatus();
    	this.statusDate = p.getStatusDate();
    	if(p.getProductLine() != null) {
        	this.productLineCode =p.getProductLine().getCode();
    	}
    	if(p.getBrand() != null) {
    		this.brandCode = p.getBrand().getCode();
    	}
    	this.reference = p.getReference();
    	this.model = p.getModel();
    	this.modelChildren = p.getModelChlidren();
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

	/**
	 * @return the productLineCode
	 */
	public String getProductLineCode() {
		return productLineCode;
	}

	/**
	 * @param productLineCode the productLineCode to set
	 */
	public void setProductLineCode(String productLineCode) {
		this.productLineCode = productLineCode;
	}

	/**
	 * @return the brandCode
	 */
	public String getBrandCode() {
		return brandCode;
	}

	/**
	 * @param brandCode the brandCode to set
	 */
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
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
	 * @return the modelChildren
	 */
	public Set<String> getModelChildren() {
		return modelChildren;
	}

	/**
	 * @param modelChildren the modelChildren to set
	 */
	public void setModelChildren(Set<String> modelChildren) {
		this.modelChildren = modelChildren;
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
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	/**
	 * @return the currentProductVersion
	 */
	public ProductVersionDto getCurrentProductVersion() {
		return currentProductVersion;
	}

	/**
	 * @param currentProductVersion the currentProductVersion to set
	 */
	public void setCurrentProductVersion(ProductVersionDto currentProductVersion) {
		this.currentProductVersion = currentProductVersion;
	}




	
	
    
}
