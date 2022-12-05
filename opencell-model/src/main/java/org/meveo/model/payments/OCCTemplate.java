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
package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.AccountingCode;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "ar_occ_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_occ_template_seq"), })
public class OCCTemplate extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * @deprecated As of version 5.0. All accountingCode now use {@link #accountingCode}
     */
    @Deprecated(since = "5.0")
    @Column(name = "account_code_client_side")
    @Size(max = 255)
    private String accountCodeClientSide;

    @Column(name = "occ_category")
    @Enumerated(EnumType.STRING)
    private OperationCategoryEnum occCategory;
    
    /**
     * journal
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private Journal journal;

    /**
     * AccountingScheme
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_scheme_id")
    private AccountingScheme accountingScheme;

    /**
     * contra accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contra_accounting_code_id")
    private AccountingCode contraAccountingCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contra_accounting_code2_id")
    private AccountingCode contraAccountingCode2;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "manual_creation_enabled")
    private boolean manualCreationEnabled;

    public String getAccountCodeClientSide() {
        return accountCodeClientSide;
    }

    public void setAccountCodeClientSide(String accountCodeClientSide) {
        this.accountCodeClientSide = accountCodeClientSide;
    }

    public OperationCategoryEnum getOccCategory() {
        return occCategory;
    }

    public void setOccCategory(OperationCategoryEnum occCategory) {
        this.occCategory = occCategory;
    }

    @Override
    public int hashCode() {
        return 961 + ("OCCTemplate" + code).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OCCTemplate)) {
            return false;
        }

        OCCTemplate other = (OCCTemplate) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }
    
    public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

    public AccountingScheme getAccountingScheme() {
        return accountingScheme;
    }

    public void setAccountingScheme(AccountingScheme accountingScheme) {
        this.accountingScheme = accountingScheme;
    }

    public AccountingCode getContraAccountingCode() {
        return contraAccountingCode;
    }

    public void setContraAccountingCode(AccountingCode contraAccountingCode) {
        this.contraAccountingCode = contraAccountingCode;
    }

    public AccountingCode getContraAccountingCode2() {
        return contraAccountingCode2;
    }

    public void setContraAccountingCode2(AccountingCode commissionAccountingCode) {
        this.contraAccountingCode2 = commissionAccountingCode;
    }

    public boolean isManualCreationEnabled() {
        return manualCreationEnabled;
    }

    public void setManualCreationEnabled(boolean manualCreationEnabled) {
        this.manualCreationEnabled = manualCreationEnabled;
    }
}
