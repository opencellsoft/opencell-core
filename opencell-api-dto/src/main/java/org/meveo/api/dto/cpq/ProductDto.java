package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.model.cpq.AgreementDateSettingEnum;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.PriceVersionDateSettingEnum;
import org.meveo.model.cpq.enums.ProductStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "CpqProductDto")
@XmlType(name = "CpqProductDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto extends BaseEntityDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2483466298983716926L;
	@Schema(description = "List of discount plan")
	private Set<DiscountPlanDto> discountList;
	@Schema(description = "Id of the product")
	protected Long id;
    @NotNull
	@Schema(description = "Code of the product", required = true)
    protected String code;
	@Schema(description = "Description of the product", required = true)
    protected String label;
	@Schema(description = "Status of the product")
	protected ProductStatusEnum status;
	@Schema(description = "Datetime of the status")
	protected Date statusDate;
	@Schema(description = "Product line code related to product")
	protected String productLineCode;
	@Schema(description = "Bran code related to product")
	protected String brandCode;
	@Schema(description = "The reference")
	protected String reference;
	@Schema(description = "The model of the product")
	protected String model;
	@Schema(description = "List of model children")
	protected Set<String> modelChildren;
	@Schema(description = "Indicate if the discount is activate", defaultValue = "false")
	protected Boolean discountFlag=Boolean.FALSE;
	@Schema(description = "Indicate if the product packaged", defaultValue = "false")
	protected Boolean packageFlag=Boolean.FALSE;
	@Schema(description = "Price version date setting")
	protected PriceVersionDateSettingEnum priceVersionDateSetting;
	
    /** The custom fields. */
	@Schema(description = "The custom fields")
    protected CustomFieldsDto customFields;
	@Schema(description = "Current product version for product")
    protected ProductVersionDto currentProductVersion;
    
    @XmlElementWrapper(name = "productChargeTemplateMappingDto")
    @XmlElement(name = "productChargeTemplateMappingDto") 
	@Schema(description = "List product charge template mapping")
    protected List<ProductChargeTemplateMappingDto> productChargeTemplateMappingDto = new ArrayList<>();
    
    
    @XmlElementWrapper(name = "commercialRuleCodes")
    @XmlElement(name = "commercialRuleCodes") 
	@Schema(description = "List commercial rule codes")
    protected List<String> commercialRuleCodes;

	@Schema(description = "List discount list code")
	protected List<String> discountListCodes=new ArrayList<String>();
    
   
	  /** The media codes. */
    @XmlElementWrapper(name = "mediaCodes")
    @XmlElement(name = "mediaCodes")
	@Schema(description = "List of media codes")
    protected Set<String> mediaCodes = new HashSet<String>();
    
    @Schema(description = "allowing to create,update and delete an product from a model")
    protected Boolean isModel=Boolean.FALSE;
    
    @Schema(description = "product model code")
    protected String productModelCode;

    @Schema(description = "Agreeemnt date Setting")
    protected AgreementDateSettingEnum agreementDateSetting;
    
    
    public ProductDto() {}
    
    public ProductDto(Product p) {
    	this.id=p.getId();
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
    	this.modelChildren = p.getModelChildren();
    	this.discountFlag = p.isDiscountFlag();
    	this.packageFlag = p.isPackageFlag();
    	this.isModel=p.getIsModel();
    	this.discountList = p.getDiscountList().stream()
				.map(dl -> new DiscountPlanDto(dl, null))
				.collect(Collectors.toSet()); 
    	if(p.getProductModel() != null)
    		this.productModelCode = p.getProductModel().getCode();
    	this.priceVersionDateSetting = p.getPriceVersionDateSetting();
    	this.agreementDateSetting = p.getAgreementDateSetting();
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
	public Boolean isDiscountFlag() {
		return discountFlag;
	}

	/**
	 * @param discountFlag the discountFlag to set
	 */
	public void setDiscountFlag(Boolean discountFlag) {
		this.discountFlag = discountFlag;
	}

	/**
	 * @return the packageFlag
	 */
	public Boolean isPackageFlag() {
		return packageFlag;
	}

	/**
	 * @param packageFlag the packageFlag to set
	 */
	public void setPackageFlag(Boolean packageFlag) {
		this.packageFlag = packageFlag;
	}

	/**
	 * @return the priceVersionDateSetting
	 */
	public PriceVersionDateSettingEnum getPriceVersionDateSetting() {
		return priceVersionDateSetting;
	}

	/**
	 * @param priceVersionDateSetting to set priceVersionDateSetting
	 */
	public void setPriceVersionDateSetting(PriceVersionDateSettingEnum priceVersionDateSetting) {
		this.priceVersionDateSetting = priceVersionDateSetting;
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
	 * @return the commercialRuleCodes
	 */
	public List<String> getCommercialRuleCodes() {
		return commercialRuleCodes;
	}

	/**
	 * @param commercialRuleCodes the commercialRuleCodes to set
	 */
	public void setCommercialRuleCodes(List<String> commercialRuleCodes) {
		this.commercialRuleCodes = commercialRuleCodes;
	}

	 
	/**
	 * @return the mediaCodes
	 */
	public Set<String> getMediaCodes() {
		return mediaCodes;
	}

	/**
	 * @param mediaCodes the mediaCodes to set
	 */
	public void setMediaCodes(Set<String> mediaCodes) {
		this.mediaCodes = mediaCodes;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Set<DiscountPlanDto> getDiscountList() {
		return discountList;
	}

	public void setDiscountList(Set<DiscountPlanDto> discountList) {
		this.discountList = discountList;
	}

	public List<String> getDiscountListCodes() {
		return discountListCodes;
	}

	public void setDiscountListCodes(List<String> discountListCodes) {
		this.discountListCodes = discountListCodes;
	}

	/**
	 * @return the productChargeTemplateMappingDto
	 */
	public List<ProductChargeTemplateMappingDto> getProductChargeTemplateMappingDto() {
		return productChargeTemplateMappingDto;
	}

	/**
	 * @param productChargeTemplateMappingDto the productChargeTemplateMappingDto to set
	 */
	public void setProductChargeTemplateMappingDto(List<ProductChargeTemplateMappingDto> productChargeTemplateMappingDto) {
		this.productChargeTemplateMappingDto = productChargeTemplateMappingDto;
	}

	public Boolean getIsModel() {
		return isModel;
	}

	public void setIsModel(Boolean isModel) {
		this.isModel = isModel;
	}

	/**
	 * @return the productModelCode
	 */
	public String getProductModelCode() {
		return productModelCode;
	}

	/**
	 * @param productModelCode the productModelCode to set
	 */
	public void setProductModelCode(String productModelCode) {
		this.productModelCode = productModelCode;
	}

	/**
	 * @return the agreementDateSetting
	 */
	public AgreementDateSettingEnum getAgreementDateSetting() {
		return agreementDateSetting;
	}

	/**
	 * @param agreementDateSetting the agreementDateSetting to set
	 */
	public void setAgreementDateSetting(AgreementDateSettingEnum agreementDateSetting) {
		this.agreementDateSetting = agreementDateSetting;
	}

	
}
