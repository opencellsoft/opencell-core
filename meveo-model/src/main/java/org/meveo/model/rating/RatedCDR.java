package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.crm.Provider;


public class RatedCDR {
	
	private Provider provider;
	
	private Date date;

	private String userCode;
	
	private String serviceCode;
	
	private BigDecimal unitAmountWithoutTax;

	private BigDecimal quantity;

	private BigDecimal amountWithoutTax;
	
	private String error;
	
	public void fillRatedTransaction(RatedTransaction ratedTransaction){
		ratedTransaction.setProvider(provider);
		ratedTransaction.setUnitAmountWithoutTax(unitAmountWithoutTax);
		ratedTransaction.setQuantity(quantity);
		ratedTransaction.setAmountWithoutTax(amountWithoutTax);
	}
	
	//getters and setters
	
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public BigDecimal getUnitAmountWithoutTax() {
		return unitAmountWithoutTax;
	}

	public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
		this.unitAmountWithoutTax = unitAmountWithoutTax;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
