package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.ProductDto;

@XmlRootElement(name = "ApplyProductRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyProductRequestDto extends BaseDto {

	private static final long serialVersionUID = 3910185882621015476L;

	@XmlElement(required = true)
	private String product;

	@XmlElement
	private String userAccount;

	@XmlElement
	private String subscription;
	
	@XmlElement(required = true)
	private Date operationDate;

	private BigDecimal quantity;

	private String description;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountWithTax;
	private String criteria1;
	private String criteria2;
	private String criteria3;
	
	private CustomFieldsDto customFields;
	
	public ApplyProductRequestDto(){
		
	}
	
	public ApplyProductRequestDto(ProductDto productDto) {
		this.amountWithoutTax=productDto.getAmountWithoutTax();
		this.amountWithTax=productDto.getAmountWithTax();
		this.criteria1=productDto.getCriteria1();
		this.criteria2=productDto.getCriteria2();
		this.criteria3=productDto.getCriteria3();
		this.description=productDto.getDescription();
		this.operationDate=productDto.getChargeDate();
		this.product=productDto.getCode();
		this.quantity=productDto.getQuantity();
		//FIXME
		//this.userAccount=productDto.get;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public String getCriteria1() {
		return criteria1;
	}

	public void setCriteria1(String criteria1) {
		this.criteria1 = criteria1;
	}

	public String getCriteria2() {
		return criteria2;
	}

	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}

	public String getCriteria3() {
		return criteria3;
	}

	public void setCriteria3(String criteria3) {
		this.criteria3 = criteria3;
	}

	@Override
	public String toString() {
		return "ApplyProductRequestDto [product=" + product + ", userAccount=" + userAccount + ", subscription=" + subscription
				 + ", operationDate=" + operationDate + ", description=" + description
				+ ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", criteria1="
				+ criteria1 + ", criteria2=" + criteria2 + ", criteria3=" + criteria3 + "]";
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}


}
