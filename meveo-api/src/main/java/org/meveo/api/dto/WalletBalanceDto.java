package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "walletBalance")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletBalanceDto {
	private String sellerCode;
	private String userAccountCode;
	private Date startDate;
	private Date endDate;
	private boolean amountWithTax;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(boolean amountWithTax) {
		this.amountWithTax = amountWithTax;
	}
}
