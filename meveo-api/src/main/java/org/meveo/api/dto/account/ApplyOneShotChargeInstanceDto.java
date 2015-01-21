package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ApplyOneShotChargeInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyOneShotChargeInstanceDto extends BaseDto {

	private static final long serialVersionUID = 3910185882621015476L;

	@XmlAttribute(required = true)
	private String oneShotChargeInstance;

	@XmlAttribute(required = true)
	private String subscription;

	@XmlAttribute(required = true)
	private Date operationDate;

	private String description;
	private BigDecimal amountWithoutTax;
	private BigDecimal amountWithTax;
	private String criteria1;
	private String criteria2;
	private String criteria3;

	public String getOneShotChargeInstance() {
		return oneShotChargeInstance;
	}

	public void setOneShotChargeInstance(String oneShotChargeInstance) {
		this.oneShotChargeInstance = oneShotChargeInstance;
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
		return "ApplyOneShotChargeInstanceDto [oneShotChargeInstance=" + oneShotChargeInstance + ", subscription="
				+ subscription + ", operationDate=" + operationDate + ", description=" + description
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
