package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.model.billing.ChargeInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceDto {

	@XmlAttribute
	private String code;

	@XmlAttribute
	private String description;

	private String status;
	private BigDecimal amountWithTax;
	private BigDecimal amountWithoutTax;
	private String sellerCode;
	private String userAccountCode;
	
	public ChargeInstanceDto() {
		
	}
	
	public ChargeInstanceDto(ChargeInstance e) {
		super();
		if (e != null) {
			this.code = e.getCode();
			this.description = e.getDescription();
			if (e.getStatus() != null) {
				this.status = e.getStatus().name();
			}
			this.amountWithTax = e.getAmountWithTax();
			this.amountWithoutTax = e.getAmountWithoutTax();
			if (e.getSeller() != null) {
				this.sellerCode = e.getSeller().getCode();
			}
			if (e.getUserAccount() != null) {
				this.userAccountCode = e.getUserAccount().getCode();
			}
		}
	}

	public ChargeInstanceDto(String code, String description, String status, BigDecimal amountWithTax, BigDecimal amountWithoutTax, String sellerCode, String userAccountCode) {
		super();
		this.code = code;
		this.description = description;
		this.status = status;
		this.amountWithTax = amountWithTax;
		this.amountWithoutTax = amountWithoutTax;
		this.sellerCode = sellerCode;
		this.userAccountCode = userAccountCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getUserAccountCode() {
		return userAccountCode;
	}

	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}

	@Override
	public String toString() {
		return "ChargeInstanceDto [code=" + code + ", description=" + description + ", status=" + status + ", amountWithTax=" + amountWithTax + ", amountWithoutTax="
				+ amountWithoutTax + ", sellerCode=" + sellerCode + ", userAccountCode=" + userAccountCode + "]";
	}

}
