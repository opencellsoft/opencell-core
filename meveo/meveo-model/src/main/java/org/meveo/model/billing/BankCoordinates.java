/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class BankCoordinates implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Column(name = "BANK_CODE", nullable = true, length = 5)
	@Size(max = 5)
	private String bankCode;

	@Column(name = "BRANCH_CODE", nullable = true, length = 5)
	@Size(max = 5)
	private String branchCode;

	@Column(name = "ACCOUNT_NUMBER", nullable = true, length = 11)
	@Size(max = 11)
	private String accountNumber;

	@Column(name = "HASH_KEY", nullable = true, length = 2)
	@Size(max = 2)
	private String key;

	@Column(name = "IBAN", length = 34)
	@Size(max = 34)
	private String iban;

	@Column(name = "BIC", length = 11)
	@Size(max = 11)
	private String bic;

	@Column(name = "ACCOUNT_OWNER", length = 50)
	@Size(max = 50)
	private String accountOwner;

	@Column(name = "BANK_NAME", length = 50)
	@Size(max = 50)
	private String bankName;

	@Column(name = "BANK_ID", length = 50)
	@Size(max = 50)
	private String bankId;

	@Column(name = "ISSUER_NUMBER", length = 50)
	@Size(max = 50)
	private String issuerNumber;

	@Column(name = "ISSUER_NAME", length = 50)
	@Size(max = 50)
	private String issuerName;

	public BankCoordinates() {
	}

	public BankCoordinates(BankCoordinates bankCoordinates) {
		this(bankCoordinates.bankCode, bankCoordinates.branchCode, bankCoordinates.accountNumber,
				bankCoordinates.key, bankCoordinates.iban, bankCoordinates.bic,
				bankCoordinates.accountOwner, bankCoordinates.bankName);
	}

	public BankCoordinates(String bankCode, String branchCode, String accountNumber, String key,
			String iban, String bic, String accountOwner, String bankName) {
		super();
		this.bankCode = bankCode;
		this.branchCode = branchCode;
		this.accountNumber = accountNumber;
		this.key = key;
		this.iban = iban;
		this.bic = bic;
		this.accountOwner = accountOwner;
		this.bankName = bankName;
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

	@Override
	public Object clone() throws CloneNotSupportedException {
		BankCoordinates o = (BankCoordinates) super.clone();
		return o;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setIssuerNumber(String issuerNumber) {
		this.issuerNumber = issuerNumber;
	}

	public String getIssuerNumber() {
		return issuerNumber;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getIssuerName() {
		return issuerName;
	}

}
