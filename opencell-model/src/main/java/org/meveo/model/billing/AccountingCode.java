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

package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Use to store Chart of accounts. Previously accounting_code fields.
 * 
 * @author Edward P. Legaspi
 * @since 5.0
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@CustomFieldEntity(cftCodePrefix = "AccountingCode")
@ExportIdentifier({ "code" })
@Entity
@Table(name = "billing_accounting_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_accounting_code_seq") })
public class AccountingCode extends EnableBusinessEntity implements ISearchable{

    private static final long serialVersionUID = -8962374797036999750L;

    /**
     * Parent accounting code
     */
    @ManyToOne
    @JoinColumn(name = "parent_accounting_code_id")
    private AccountingCode parentAccountingCode;

    /**
     * Accounting type
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chart_of_account_type", length = 25)
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    /**
     * Reporting account
     */
    @Column(name = "reporting_account", length = 50)
    private String reportingAccount;

    /**
     * Type of view
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chart_of_account_view_type", length = 25)
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    /**
     * Notes
     */
    @Column(name = "notes", length = 2000)
    private String notes;

    /**
     * Was record migrated
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "migrated", nullable = false)
    private boolean migrated = false;

    public AccountingCode getParentAccountingCode() {
        return parentAccountingCode;
    }

    public void setParentAccountingCode(AccountingCode parentAccountingCode) {
        this.parentAccountingCode = parentAccountingCode;
    }

    public ChartOfAccountTypeEnum getChartOfAccountTypeEnum() {
        return chartOfAccountTypeEnum;
    }

    public void setChartOfAccountTypeEnum(ChartOfAccountTypeEnum chartOfAccountTypeEnum) {
        this.chartOfAccountTypeEnum = chartOfAccountTypeEnum;
    }

    public String getReportingAccount() {
        return reportingAccount;
    }

    public void setReportingAccount(String reportingAccount) {
        this.reportingAccount = reportingAccount;
    }

    public ChartOfAccountViewTypeEnum getChartOfAccountViewTypeEnum() {
        return chartOfAccountViewTypeEnum;
    }

    public void setChartOfAccountViewTypeEnum(ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum) {
        this.chartOfAccountViewTypeEnum = chartOfAccountViewTypeEnum;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

}
