package org.meveo.api.dto.cpq;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.contract.ContractItem;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
public class ContractItemDto extends BusinessEntityDto {

    @NotNull
	private String contractCode;
	private String offerTemplateCode;
	private String productCode;
    @NotNull
	private String pricePlanCode;
	private String chargeTemplateCode;
	private String serviceTemplateCode;
	private int rate;
    private BigDecimal amountWithoutTax;
    
    public ContractItemDto() {}
    
    public ContractItemDto(ContractItem c) {
    	if(c.getContract() != null)
    		this.contractCode = c.getContract().getCode();
    	if(c.getOfferTemplate() != null)
    		this.offerTemplateCode = c.getOfferTemplate().getCode();
    	if(c.getProduct() != null)
    		this.productCode = c.getProduct().getCode();
    	if(c.getPricePlan() != null)
    		this.pricePlanCode = c.getPricePlan().getCode();
    	if(c.getChargeTemplate() != null)
    		this.chargeTemplateCode = c.getChargeTemplate().getCode();
    	if(c.getServiceTemplate() != null)
    		this.serviceTemplateCode = c.getServiceTemplate().getCode();
    	this.rate = c.getRate();
    	this.amountWithoutTax = c.getAmountWithoutTax();
    	this.description = c.getDescription();
    	this.code = c.getCode();
    }
    
	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}
	/**
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	/**
	 * @return the offerTemplateCode
	 */
	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}
	/**
	 * @param offerTemplateCode the offerTemplateCode to set
	 */
	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
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
	 * @return the pricePlanCode
	 */
	public String getPricePlanCode() {
		return pricePlanCode;
	}
	/**
	 * @param pricePlanCode the pricePlanCode to set
	 */
	public void setPricePlanCode(String pricePlanCode) {
		this.pricePlanCode = pricePlanCode;
	}
	/**
	 * @return the chargeTemplateCode
	 */
	public String getChargeTemplateCode() {
		return chargeTemplateCode;
	}
	/**
	 * @param chargeTemplateCode the chargeTemplateCode to set
	 */
	public void setChargeTemplateCode(String chargeTemplateCode) {
		this.chargeTemplateCode = chargeTemplateCode;
	}
	/**
	 * @return the serviceTemplateCode
	 */
	public String getServiceTemplateCode() {
		return serviceTemplateCode;
	}
	/**
	 * @param serviceTemplateCode the serviceTemplateCode to set
	 */
	public void setServiceTemplateCode(String serviceTemplateCode) {
		this.serviceTemplateCode = serviceTemplateCode;
	}
	/**
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}
	/**
	 * @return the amountWithoutTax
	 */
	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}
	/**
	 * @param amountWithoutTax the amountWithoutTax to set
	 */
	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}



    
	
}
