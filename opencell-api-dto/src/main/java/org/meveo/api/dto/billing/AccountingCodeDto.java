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

package org.meveo.api.dto.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.meveo.api.dto.AuditableDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.ChartOfAccountTypeEnum;
import org.meveo.model.billing.ChartOfAccountViewTypeEnum;

/**
 * The Class AccountingCodeDto.
 *
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.0
 */
public class AccountingCodeDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8093532321068428023L;

    /** The parent accounting code. */
    private String parentAccountingCode;

    /** The chart of account type enum. */
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    /** The chart of account view type enum. */
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    /** The reporting account. */
    private String reportingAccount;

    /** The notes. */
    private String notes;

    /** The migrated. */
    private boolean migrated = false;

    @JsonIgnore
    private AuditableDto auditableNullSafe;

    /**
     * Instantiates a new AccountingCodeDto
     */
    public AccountingCodeDto() {
    }

    /**
     * Converts AccountingCode JPA entity to a DTO
     * 
     * @param accountingCode Entity to convert
     */
    public AccountingCodeDto(AccountingCode accountingCode) {
        super(accountingCode);

        setChartOfAccountTypeEnum(accountingCode.getChartOfAccountTypeEnum());
        setChartOfAccountViewTypeEnum(accountingCode.getChartOfAccountViewTypeEnum());
        setNotes(accountingCode.getNotes());
        setReportingAccount(accountingCode.getReportingAccount());
        setMigrated(accountingCode.isMigrated());
    }

    /**
     * Gets the parent accounting code.
     *
     * @return the parent accounting code
     */
    public String getParentAccountingCode() {
        return parentAccountingCode;
    }

    /**
     * Sets the parent accounting code.
     *
     * @param parentAccountingCode the new parent accounting code
     */
    public void setParentAccountingCode(String parentAccountingCode) {
        this.parentAccountingCode = parentAccountingCode;
    }

    /**
     * Gets the chart of account type enum.
     *
     * @return the chart of account type enum
     */
    public ChartOfAccountTypeEnum getChartOfAccountTypeEnum() {
        return chartOfAccountTypeEnum;
    }

    /**
     * Sets the chart of account type enum.
     *
     * @param chartOfAccountTypeEnum the new chart of account type enum
     */
    public void setChartOfAccountTypeEnum(ChartOfAccountTypeEnum chartOfAccountTypeEnum) {
        this.chartOfAccountTypeEnum = chartOfAccountTypeEnum;
    }

    /**
     * Gets the reporting account.
     *
     * @return the reporting account
     */
    public String getReportingAccount() {
        return reportingAccount;
    }

    /**
     * Sets the reporting account.
     *
     * @param reportingAccount the new reporting account
     */
    public void setReportingAccount(String reportingAccount) {
        this.reportingAccount = reportingAccount;
    }

    /**
     * Gets the chart of account view type enum.
     *
     * @return the chart of account view type enum
     */
    public ChartOfAccountViewTypeEnum getChartOfAccountViewTypeEnum() {
        return chartOfAccountViewTypeEnum;
    }

    /**
     * Sets the chart of account view type enum.
     *
     * @param chartOfAccountViewTypeEnum the new chart of account view type enum
     */
    public void setChartOfAccountViewTypeEnum(ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum) {
        this.chartOfAccountViewTypeEnum = chartOfAccountViewTypeEnum;
    }

    /**
     * Gets the notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     *
     * @param notes the new notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Checks if is migrated.
     *
     * @return true, if is migrated
     */
    public boolean isMigrated() {
        return migrated;
    }

    /**
     * Sets the migrated.
     *
     * @param migrated the new migrated
     */
    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }
}
