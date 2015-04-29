package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.billing.BankCoordinates;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "BankCoordinates")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankCoordinatesDto implements Serializable {

	private static final long serialVersionUID = 4452076136683484895L;

	private String bankCode;
	private String branchCode;
	private String accountNumber;
	private String key;
	private String iban;
	private String bic;
	private String accountOwner;
	private String bankName;
	private String bankId;
	private String issuerNumber;
	private String issuerName;
	private String ics;

	public BankCoordinatesDto() {

	}

	public BankCoordinatesDto(BankCoordinates e) {
		bankCode = e.getBankCode();
		branchCode = e.getBranchCode();
		accountNumber = e.getAccountNumber();
		key = e.getKey();
		iban = e.getIban();
		bic = e.getBic();
		accountOwner = e.getAccountOwner();
		bankName = e.getBankName();
		bankId = e.getBankId();
		issuerNumber = e.getIssuerNumber();
		issuerName = e.getIssuerName();
		ics = e.getIcs();
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getIssuerNumber() {
		return issuerNumber;
	}

	public void setIssuerNumber(String issuerNumber) {
		this.issuerNumber = issuerNumber;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getIcs() {
		return ics;
	}

	public void setIcs(String ics) {
		this.ics = ics;
	}

	@Override
	public String toString() {
		return "BankCoordinatesDto [bankCode=" + bankCode + ", branchCode=" + branchCode + ", accountNumber=" + accountNumber + ", key=" + key + ", iban=" + iban + ", bic=" + bic
				+ ", accountOwner=" + accountOwner + ", bankName=" + bankName + ", bankId=" + bankId + ", issuerNumber=" + issuerNumber + ", issuerName=" + issuerName + ", ics="
				+ ics + "]";
	}

}
