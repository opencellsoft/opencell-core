/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.StringUtils;

/**
 * Bank account information
 * 
 * @author Andrius Karpavicius
 */
@Embeddable
public class BankCoordinates implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Bank code
     */
    @Column(name = "bank_code", length = 5)
    @Size(max = 5)
    private String bankCode;

    /**
     * Branch code
     */
    @Column(name = "branch_code", length = 5)
    @Size(max = 5)
    private String branchCode;

    /**
     * Account number
     */
    @Column(name = "account_number", length = 11)
    @Size(max = 11)
    private String accountNumber;

    /**
     * Key
     */
    @Column(name = "hash_key", length = 2)
    @Size(max = 2)
    private String key;

    /**
     * IBAN number
     */
    @Column(name = "iban", length = 34)
    @Size(max = 34)
    private String iban;

    /**
     * BIC number
     */
    @Column(name = "bic", length = 11)
    @Size(max = 11)
    private String bic;

    /**
     * Account owner name
     */
    @Column(name = "account_owner", length = 50)
    @Size(max = 50)
    private String accountOwner;

    /**
     * Bank name
     */
    @Column(name = "bank_name", length = 50)
    @Size(max = 50)
    private String bankName;

    /**
     * Bank identifier
     */
    @Column(name = "bank_id", length = 50)
    @Size(max = 50)
    private String bankId;

    /**
     * Issuer number
     */
    @Column(name = "issuer_number", length = 50)
    @Size(max = 50)
    private String issuerNumber;

    /**
     * Issuer name
     */
    @Column(name = "issuer_name", length = 50)
    @Size(max = 50)
    private String issuerName;

    /**
     * ICS number. L'identifiant Créancier Sepa
     */
    @Column(name = "ics", length = 35)
    @Size(max = 35)
    private String ics;

    public BankCoordinates() {
    }

    public BankCoordinates(BankCoordinates bankCoordinates) {
        this(bankCoordinates.bankCode, bankCoordinates.branchCode, bankCoordinates.accountNumber, bankCoordinates.key, bankCoordinates.iban, bankCoordinates.bic,
            bankCoordinates.accountOwner, bankCoordinates.bankName);
    }

    public BankCoordinates(String bankCode, String branchCode, String accountNumber, String key, String iban, String bic, String accountOwner, String bankName) {
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

    public String getIcs() {
        return ics;
    }

    public void setIcs(String ics) {
        this.ics = ics;
    }

    @Override
    public String toString() {
        return "BankCoordinates [bankCode=" + bankCode + ", branchCode=" + branchCode + ", accountNumber=" + accountNumber + ", key=" + key + ", iban=" + iban + ", bic=" + bic
                + ", accountOwner=" + accountOwner + ", bankName=" + bankName + ", bankId=" + bankId + ", issuerNumber=" + issuerNumber + ", issuerName=" + issuerName + ", ics="
                + ics + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof BankCoordinates)) {
            return false;
        }

        BankCoordinates other = (BankCoordinates) obj;

        return StringUtils.compare(iban, other.getIban()) == 0;
    }
}
