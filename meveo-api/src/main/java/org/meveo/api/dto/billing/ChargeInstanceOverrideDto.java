package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ChargeInstanceOverride")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceOverrideDto implements Serializable {

	private static final long serialVersionUID = 5653231200327069483L;

	@XmlAttribute(required = true)
	private String chargeInstanceCode;

	@XmlElement(required = true)
	private BigDecimal amountWithoutTax;

	@XmlElement(required = false)
	private BigDecimal amountWithTax;

	public String getChargeInstanceCode() {
		return chargeInstanceCode;
	}

	public void setChargeInstanceCode(String chargeInstanceCode) {
		this.chargeInstanceCode = chargeInstanceCode;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	@Override
	public String toString() {
		return "ChargeInstanceOverrideDto [chargeInstanceCode=" + chargeInstanceCode + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + "]";
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

}
