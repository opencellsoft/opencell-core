package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ApplyOneShotChargeInstanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyOneShotChargeInstanceRequestDto extends BaseDto {

	private static final long serialVersionUID = 3910185882621015476L;

	@XmlElement(required = true)
	private String oneShotCharge;

	@XmlElement(required = true)
	private String subscription;

	private String wallet;

	private Boolean createWallet;

	@XmlElement(required = true)
	private Date operationDate;

	private BigDecimal quantity;

	private String description;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountWithTax;
	private String criteria1;
	private String criteria2;
	private String criteria3;

	public String getOneShotCharge() {
		return oneShotCharge;
	}

	public void setOneShotCharge(String oneShotCharge) {
		this.oneShotCharge = oneShotCharge;
	}

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}

	public Boolean getCreateWallet() {
		return createWallet;
	}

	public void setCreateWallet(Boolean createWallet) {
		this.createWallet = createWallet;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
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
		return "ApplyOneShotChargeInstanceDto [oneShotCharge=" + oneShotCharge + ", subscription=" + subscription
				+ ", wallet=" + wallet + ", operationDate=" + operationDate + ", description=" + description
				+ ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", criteria1="
				+ criteria1 + ", criteria2=" + criteria2 + ", criteria3=" + criteria3 + "]";
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

}
