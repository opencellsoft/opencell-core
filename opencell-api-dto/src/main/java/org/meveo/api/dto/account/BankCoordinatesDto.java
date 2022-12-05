/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.account;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.billing.BankCoordinates;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class BankCoordinatesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BankCoordinates")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankCoordinatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4452076136683484895L;

    /** The bank code. */
    @NotNull
    @Size(max = 5)
    @Schema(description = "The bank code", required = true)
    private String bankCode;

    /** The branch code. */
    @NotNull
    @Size(max = 5)
    @Schema(description = "The branch code", required = true)
    private String branchCode;

    /** The account number. */
    @NotNull
    @Size(max = 11)
    @Schema(description = "The account number", required = true)
    private String accountNumber;

    /** The key. */
    @NotNull
    @Size(max = 2)
    @Schema(description = "The key", required = true)
    private String key;

    /** The iban. */
    @NotNull
    @Size(max = 34)
    @Schema(description = "The iban", required = true)
    private String iban;

    /** The bic. */
    @NotNull
    @Size(max = 11)
    @Schema(description = "The bic", required = true)
    private String bic;

    /** The account owner. */
    @NotNull
    @Size(max = 50)
    @Schema(description = "The account owner", required = true)
    private String accountOwner;

    /** The bank name. */
    @NotNull
    @Size(max = 50)
    @Schema(description = "The bank name", required = true)
    private String bankName;

    /** The bank id. */
    @Size(max = 50)
    @Schema(description = "The bank id")
    private String bankId;

    /** The issuer number. */
    @Size(max = 50)
    @Schema(description = "The issuer number")
    private String issuerNumber;

    /** The issuer name. */
    @Size(max = 50)
    @Schema(description = "The issuer name")
    private String issuerName;

    /** The ics. */
    @Size(max = 35)
    @Schema(description = "The ics")
    private String ics;

    /**
     * Instantiates a new bank coordinates dto.
     */
    public BankCoordinatesDto() {

    }

    /**
     * Instantiates a new bank coordinates dto.
     *
     * @param bankCoordinates the bankCoordinates entity
     */
    public BankCoordinatesDto(BankCoordinates bankCoordinates) {
        if (bankCoordinates == null) {
            return;
        }
        bankCode = bankCoordinates.getBankCode();
        branchCode = bankCoordinates.getBranchCode();
        accountNumber = bankCoordinates.getAccountNumber();
        key = bankCoordinates.getKey();
        iban = bankCoordinates.getIban();
        bic = bankCoordinates.getBic();
        accountOwner = bankCoordinates.getAccountOwner();
        bankName = bankCoordinates.getBankName();
        bankId = bankCoordinates.getBankId();
        issuerNumber = bankCoordinates.getIssuerNumber();
        issuerName = bankCoordinates.getIssuerName();
        ics = bankCoordinates.getIcs();
    }

    /**
     * Gets the bank code.
     *
     * @return the bank code
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bank code.
     *
     * @param bankCode the new bank code
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * Gets the branch code.
     *
     * @return the branch code
     */
    public String getBranchCode() {
        return branchCode;
    }

    /**
     * Sets the branch code.
     *
     * @param branchCode the new branch code
     */
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    /**
     * Gets the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the account number.
     *
     * @param accountNumber the new account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the iban.
     *
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the iban.
     *
     * @param iban the new iban
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Gets the bic.
     *
     * @return the bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * Sets the bic.
     *
     * @param bic the new bic
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Gets the account owner.
     *
     * @return the account owner
     */
    public String getAccountOwner() {
        return accountOwner;
    }

    /**
     * Sets the account owner.
     *
     * @param accountOwner the new account owner
     */
    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    /**
     * Gets the bank name.
     *
     * @return the bank name
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the bank name.
     *
     * @param bankName the new bank name
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * Gets the bank id.
     *
     * @return the bank id
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * Sets the bank id.
     *
     * @param bankId the new bank id
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    /**
     * Gets the issuer number.
     *
     * @return the issuer number
     */
    public String getIssuerNumber() {
        return issuerNumber;
    }

    /**
     * Sets the issuer number.
     *
     * @param issuerNumber the new issuer number
     */
    public void setIssuerNumber(String issuerNumber) {
        this.issuerNumber = issuerNumber;
    }

    /**
     * Gets the issuer name.
     *
     * @return the issuer name
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * Sets the issuer name.
     *
     * @param issuerName the new issuer name
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * Gets the ics.
     *
     * @return the ics
     */
    public String getIcs() {
        return ics;
    }

    /**
     * Sets the ics.
     *
     * @param ics the new ics
     */
    public void setIcs(String ics) {
        this.ics = ics;
    }

    /**
     * From dto.
     *
     * @return the bank coordinates
     */
    public BankCoordinates fromDto() {
        BankCoordinates bankCoordinates = new BankCoordinates();
        bankCoordinates.setBankCode(getBankCode());
        bankCoordinates.setBranchCode(getBranchCode());
        bankCoordinates.setAccountNumber(getAccountNumber());
        bankCoordinates.setKey(getKey());
        bankCoordinates.setIban(getIban());
        bankCoordinates.setBic(getBic());
        bankCoordinates.setAccountOwner(getAccountOwner());
        bankCoordinates.setBankName(getBankName());
        bankCoordinates.setBankId(getBankId());
        bankCoordinates.setIssuerNumber(getIssuerNumber());
        bankCoordinates.setIssuerName(getIssuerName());
        bankCoordinates.setIcs(getIcs());

        return bankCoordinates;
    }


    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return StringUtils.isBlank(iban);
    }
    
    @Override
    public String toString() {
        return "BankCoordinatesDto [bankCode=" + bankCode + ", branchCode=" + branchCode + ", accountNumber=" + accountNumber + ", key=" + key + ", iban=" + iban + ", bic=" + bic
                + ", accountOwner=" + accountOwner + ", bankName=" + bankName + ", bankId=" + bankId + ", issuerNumber=" + issuerNumber + ", issuerName=" + issuerName + ", ics="
                + ics + "]";
    }
}