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

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.payment.AccountingSchemeDto;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;

/**
 * The Class OccTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "OCCTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2587489734648000805L;

    /** The accounting code. */
    @XmlElement(required = true)
    private String accountingCode;

    /** The account code. */
    @Deprecated
    private String accountCode;

    /** The occ category. */
    @XmlElement(required = true)
    private OperationCategoryEnum occCategory;

    /**
     * The account code client side.
     *
     * @deprecated duplicate of accountingCode.
     */
    @Deprecated
    private String accountCodeClientSide;
    
    /** The journal code. */
    @Schema(description = "The journal code")
    private String journalCode;

    /** The accounting scheme. */
    @XmlElement
    private AccountingSchemeDto accountingScheme;

    /** contract accounting code */
    @Schema(description = "contract accounting code")
    @XmlElement(name = "contraAccountingCode")
    private String contractAccountingCode;

    /** commission accounting code */
    @Schema(description = "contra Accounting Code 2")
    @XmlElement(name = "contraAccountingCode2")
    private String contraAccountingCode2;
    
    @Schema(description = "Flag manual creation enabled")
    private Boolean manualCreationEnabled;

    /**
     * Instantiates a new occ template dto.
     */
    public OccTemplateDto() {
    }

    /**
     * Instantiates a new occ template dto.
     *
     * @param occTemplate the OCCTemplate entity
     */
    public OccTemplateDto(OCCTemplate occTemplate) {
        super(occTemplate);
        accountingCode = occTemplate.getAccountingCode().getCode();
        accountCode = occTemplate.getAccountingCode().getCode();
        occCategory = occTemplate.getOccCategory();
        accountCodeClientSide = occTemplate.getAccountCodeClientSide();
        manualCreationEnabled = occTemplate.isManualCreationEnabled();
        if (occTemplate.getAccountingScheme() != null) {
            accountingScheme = new AccountingSchemeDto(occTemplate.getAccountingScheme());
        }
        if(occTemplate.getContraAccountingCode() != null) {
            contractAccountingCode = occTemplate.getContraAccountingCode().getCode();
        }

        if(occTemplate.getContraAccountingCode2() != null) {
            contraAccountingCode2 = occTemplate.getContraAccountingCode2().getCode();
        }
    }

    /**
     * Gets the occ category.
     *
     * @return the occ category
     */
    public OperationCategoryEnum getOccCategory() {
        return occCategory;
    }

    /**
     * Sets the occ category.
     *
     * @param occCategory the new occ category
     */
    public void setOccCategory(OperationCategoryEnum occCategory) {
        this.occCategory = occCategory;
    }

    /**
     * Gets the account code client side.
     *
     * @return the account code client side
     */
    public String getAccountCodeClientSide() {
        return accountCodeClientSide;
    }

    /**
     * Sets the account code client side.
     *
     * @param accountCodeClientSide the new account code client side
     */
    public void setAccountCodeClientSide(String accountCodeClientSide) {
        this.accountCodeClientSide = accountCodeClientSide;
    }

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OccTemplateDto [accountingCode=" + accountingCode + ", occCategory=" + occCategory + ", accountCodeClientSide=" + accountCodeClientSide + "]";
    }

    /**
     * Gets the account code.
     *
     * @return the account code
     */
    public String getAccountCode() {
        return accountCode;
    }

    /**
     * Sets the account code.
     *
     * @param accountCode the new account code
     */
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    /**
     * Gets journal code.
     *
     * @return the journal code
     */
    public String getJournalCode() {
		return journalCode;
	}

    /**
     * Sets journal code.
     *
     * @param journalCode the journal code
     */
    public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

    /**
     * Gets accounting scheme.
     *
     * @return the accounting scheme
     */
    public AccountingSchemeDto getAccountingScheme() {
        return accountingScheme;
    }

    /**
     * Sets accounting scheme.
     *
     * @param accountingScheme the accounting scheme
     */
    public void setAccountingScheme(AccountingSchemeDto accountingScheme) {
        this.accountingScheme = accountingScheme;
    }

    public String getContractAccountingCode() {
        return contractAccountingCode;
    }

    public void setContractAccountingCode(String contractAccountingCode) {
        this.contractAccountingCode = contractAccountingCode;
    }

    public String getContraAccountingCode2() {
        return contraAccountingCode2;
    }

    public void setContraAccountingCode2(String contraAccountingCode2) {
        this.contraAccountingCode2 = contraAccountingCode2;
    }

	public Boolean isManualCreationEnabled() {
		return manualCreationEnabled;
	}

	public void setManualCreationEnabled(Boolean manualCreationEnabled) {
		this.manualCreationEnabled = manualCreationEnabled;
	}
    
}